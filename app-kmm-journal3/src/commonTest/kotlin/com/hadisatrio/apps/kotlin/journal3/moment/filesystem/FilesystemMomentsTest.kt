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

package com.hadisatrio.apps.kotlin.journal3.moment.filesystem

import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.story.SelfPopulatingStories
import com.hadisatrio.apps.kotlin.journal3.story.filesystem.FilesystemStories
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.AfterTest
import kotlin.test.Test

class FilesystemMomentsTest {

    private val fileSystem = FakeFileSystem()
    private val stories = FilesystemStories(fileSystem, "content".toPath())

    @AfterTest
    fun `Closes all file streams`() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `Writes new moments to the filesystem`() {
        val story = stories.new()
        val moment = story.moments.new()

        moment.update(TokenableString("FizzBuzz"))
        moment.update(Sentiment(1.0F))
        moment.update(Timestamp("2019-07-07T20:00:00+07:00"))

        story.moments.shouldHaveSize(1)
        moment.description.shouldBe(TokenableString("FizzBuzz"))
        moment.sentiment.shouldBe(Sentiment(1.0F))
        moment.timestamp.shouldBe(Timestamp("2019-07-07T20:00:00+07:00"))
        fileSystem.metadata("content/${story.id}/moments/${moment.id}".toPath()).isRegularFile.shouldBeTrue()
    }

    @Test
    fun `Finds a moment by its ID`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 10, stories)
        val story = stories.first()
        val moment = story.moments.first()

        val found = story.moments.find(moment.id)

        found.shouldHaveSize(1)
        moment.id.shouldBe(found.first().id)
    }

    @Test
    fun `Returns empty iterable when asked to find a non-existent moment by ID`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 10, stories)
        val story = stories.first()
        val found = story.moments.find(uuid4())

        found.shouldBeEmpty()
    }

    @Test
    fun `Deletes forgotten moments from the filesystem`() {
        val story = stories.new()
        val moment = story.moments.new()

        moment.forget()

        fileSystem.exists("content/${story.id}".toPath()).shouldBeTrue()
        fileSystem.exists("content/${story.id}/moments/${moment.id}".toPath()).shouldBeFalse()
    }
}
