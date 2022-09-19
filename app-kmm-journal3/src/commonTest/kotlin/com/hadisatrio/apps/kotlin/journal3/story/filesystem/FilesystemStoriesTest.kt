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
import com.chrynan.uri.core.Uri
import com.chrynan.uri.core.fromString
import com.hadisatrio.apps.kotlin.journal3.story.SelfPopulatingStories
import com.hadisatrio.apps.kotlin.journal3.uri.IllegalUriException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.AfterTest
import kotlin.test.Test

class FilesystemStoriesTest {

    private val fileSystem = FakeFileSystem()
    private val stories = FilesystemStories(fileSystem, "content".toPath())

    @AfterTest
    fun `Closes all file streams`() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `Writes new stories to the filesystem`() {
        val story = stories.new()

        story.update("Foo")

        stories.shouldHaveSize(1)
        story.title.shouldBe("Foo")
        fileSystem.metadata("content/${story.id}".toPath()).isDirectory.shouldBeTrue()
    }

    @Test
    fun `Finds story by URI if one exists`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 1, origin = stories)
        val story = stories.first()
        val uri = Uri.fromString("journal3://stories/${story.id}")

        val found = stories.findStory(uri)

        found.shouldHaveSize(1)
        found.first().id.shouldBe(story.id)
    }

    @Test
    fun `Returns an empty iterable when asked to find an non-existent story by URI`() {
        val uri = Uri.fromString("journal3://stories/${uuid4()}")

        val found = stories.findStory(uri)

        found.shouldBeEmpty()
    }

    @Test
    fun `Throws IllegalUriException when given an invalid URI to find stories with`() {
        val invalidUris = setOf(
            Uri.fromString("https://stories/${uuid4()}"),
            Uri.fromString("journal2://stories/${uuid4()}"),
            Uri.fromString("journal3://stories"),
            Uri.fromString("journal3://story/${uuid4()}"),
            Uri.fromString("journal3://moments/${uuid4()}")
        )

        invalidUris.forEach { shouldThrow<IllegalUriException> { stories.findStory(it) } }
    }

    @Test
    fun `Returns an empty iterable when asked to find an non-existent moment by URI`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 1, origin = stories)
        val story = stories.first()
        val uri = Uri.fromString("journal3://stories/${story.id}/moments/${uuid4()}")

        val found = stories.findMoments(uri)

        found.shouldBeEmpty()
    }

    @Test
    fun `Finds a moment by URI if one exists`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 1, origin = stories)
        val story = stories.first()
        val moment = story.moments.first()
        val uri = Uri.fromString("journal3://stories/${story.id}/moments/${moment.id}")

        val found = stories.findMoments(uri)

        found.shouldHaveSize(1)
        found.first().id.shouldBe(moment.id)
    }

    @Test
    fun `Finds all moments from a story URI if they exists`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 10, origin = stories)
        val story = stories.first()
        val uri = Uri.fromString("journal3://stories/${story.id}")

        val found = stories.findMoments(uri)

        found.shouldHaveSize(10)
    }

    @Test
    fun `Throws IllegalUriException when given an invalid URI to find moments with`() {
        val invalidUris = setOf(
            Uri.fromString("https://stories/${uuid4()}"),
            Uri.fromString("journal2://stories/${uuid4()}"),
            Uri.fromString("journal3://story/${uuid4()}"),
            Uri.fromString("journal3://moments/${uuid4()}")
        )

        invalidUris.forEach { shouldThrow<IllegalUriException> { stories.findMoments(it) } }
    }
}
