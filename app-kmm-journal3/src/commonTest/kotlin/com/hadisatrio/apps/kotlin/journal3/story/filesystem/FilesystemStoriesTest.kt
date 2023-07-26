/*
 * Copyright (C) 2022 Hadi Satrio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hadisatrio.apps.kotlin.journal3.story.filesystem

import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.kotlin.journal3.moment.MemorablesCollection
import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMemorablePlaces
import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMentionedPeople
import com.hadisatrio.apps.kotlin.journal3.story.SelfPopulatingStories
import com.hadisatrio.apps.kotlin.journal3.story.Story
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.shouldBe
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.AfterTest
import kotlin.test.Test

class FilesystemStoriesTest {

    private val fileSystem = FakeFileSystem()
    private val places = FilesystemMemorablePlaces(fileSystem, "content/places".toPath())
    private val people = FilesystemMentionedPeople(fileSystem, "content/people".toPath())
    private val memorables = MemorablesCollection(places, people)
    private val stories = FilesystemStories(fileSystem, "content/stories".toPath(), memorables)

    @AfterTest
    fun `Closes all file streams`() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `Collects moments from each of its stories`() {
        val stories = SelfPopulatingStories(noOfStories = 2, noOfMoments = 5, stories)
        stories.moments.shouldHaveSize(10)
    }

    @Test
    fun `Writes new stories to the filesystem`() {
        val story = stories.new()

        story.update("Foo")

        stories.shouldHaveSize(1)
        story.title.shouldBe("Foo")
        fileSystem.metadata("content/stories/${story.id}".toPath()).isDirectory.shouldBeTrue()
    }

    @Test
    fun `Tells whether or not it contains any moments within`() {
        val empty = FilesystemStories(fileSystem, "Foo", memorables)
        val nonEmpty = SelfPopulatingStories(
            noOfStories = 2,
            noOfMoments = 1,
            FilesystemStories(fileSystem, "Bar", memorables)
        )
        nonEmpty.first().moments.first().forget()

        empty.hasMoments().shouldBeFalse()
        nonEmpty.hasMoments().shouldBeTrue()
    }

    @Test
    fun `Reports whether or not it contains a story basing on its ID`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 1, stories)
        val story = stories.first()

        stories.containsStory(story.id).shouldBeTrue()
        stories.containsStory(uuid4()).shouldBeFalse()
    }

    @Test
    fun `Finds a story by its ID`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 1, stories)
        val story = stories.first()

        val found = stories.findStory(story.id)

        found.shouldHaveSize(1)
        story.id.shouldBe(found.first().id)
    }

    @Test
    fun `Returns empty iterable when asked to find a non-existent story by ID`() {
        val found = stories.findStory(uuid4())

        found.shouldBeEmpty()
    }

    @Test
    fun `Reports whether or not it contains a moment basing on its ID`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 1, stories)
        val moment = stories.first().moments.first()

        stories.containsMoment(moment.id).shouldBeTrue()
        stories.containsMoment(uuid4()).shouldBeFalse()
    }

    @Test
    fun `Finds a moment by its ID`() {
        val stories = SelfPopulatingStories(noOfStories = 10, noOfMoments = 10, stories)
        val story = stories.first()
        val moment = story.moments.first()

        val found = stories.findMoment(moment.id)

        found.shouldHaveSize(1)
        moment.id.shouldBe(found.first().id)
    }

    @Test
    fun `Returns empty iterable when asked to find a non-existent moment by ID`() {
        val stories = SelfPopulatingStories(noOfStories = 10, noOfMoments = 10, stories)
        val found = stories.findMoment(uuid4())

        found.shouldBeEmpty()
    }

    @Test
    fun `Fetches its most recent moment`() {
        val stories = SelfPopulatingStories(noOfStories = 9, noOfMoments = 10, stories)
        stories.new().update("Foo") // ...add a story with no moments to increase variety.

        val mostRecentMoment = stories.mostRecentMoment()

        stories.flatMap { it.moments }.filterNot { it.id == mostRecentMoment.id }.forEach { other ->
            other.timestamp.compareTo(mostRecentMoment.timestamp).shouldBeLessThan(0)
        }
    }

    @Test
    fun `Iterates through moments by ascending order of their titles`() {
        repeat(10) {
            val randomTitle = uuid4().toString()
            stories.new().apply { update(randomTitle) }
        }

        var previous: Story? = null
        stories.forEach { current ->
            if (previous != null) current.compareTo(previous!!).shouldBeGreaterThanOrEqual(0)
            previous = current
        }
    }
}
