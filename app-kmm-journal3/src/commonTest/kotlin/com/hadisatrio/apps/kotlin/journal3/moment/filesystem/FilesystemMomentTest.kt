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

import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.story.filesystem.FilesystemStories
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.ints.shouldBeNegative
import io.kotest.matchers.ints.shouldBePositive
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import okio.use
import kotlin.test.AfterTest
import kotlin.test.Test

class FilesystemMomentTest {

    private val fileSystem = FakeFileSystem()
    private val stories = FilesystemStories(fileSystem, "content".toPath())
    private val story = stories.new()
    private val moments = story.moments

    @AfterTest
    fun `Closes all file streams`() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `Returns expected default values`() {
        val moment = moments.new()

        moment.timestamp.shouldBe(Timestamp(Instant.fromEpochMilliseconds(0)))
        moment.description.shouldBe(TokenableString(""))
        moment.sentiment.shouldBe(Sentiment(0.123456789F))
    }

    @Test
    fun `Write updates to the filesystem`() {
        val moment = moments.new()

        moment.update(timestamp = Timestamp(Instant.fromEpochMilliseconds(1000)))
        moment.update(description = TokenableString("Foo"))
        moment.update(sentiment = Sentiment(0.5F))

        val path = "content/${story.id}/moments/${moment.id}".toPath()
        val fileContent = fileSystem.source(path).buffer().use { it.readUtf8() }
        fileContent.contains("Foo")
        fileContent.contains("0.5")
        moment.timestamp.shouldBe(Timestamp(Instant.fromEpochMilliseconds(1000)))
        moment.description.shouldBe(TokenableString("Foo"))
        moment.sentiment.shouldBe(Sentiment(0.5F))
    }

    @Test
    fun `Deletes itself from the filesystem`() {
        val moment = moments.new()

        moment.forget()

        val path = "content/${story.id}/moments/${moment.id}".toPath()
        fileSystem.exists(path).shouldBeFalse()
    }

    @Test
    fun `Compares itself to others based on timestamp`() {
        val self = moments.new()
        val newer = moments.new().apply { update(Timestamp(Instant.DISTANT_FUTURE)) }
        val older = moments.new().apply { update(Timestamp(Instant.DISTANT_PAST)) }

        self.compareTo(newer).shouldBeNegative()
        self.compareTo(older).shouldBePositive()
    }
}
