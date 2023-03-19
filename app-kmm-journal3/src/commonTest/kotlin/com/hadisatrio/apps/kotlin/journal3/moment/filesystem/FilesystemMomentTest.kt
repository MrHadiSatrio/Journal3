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
import com.hadisatrio.libs.kotlin.geography.NullIsland
import com.hadisatrio.libs.kotlin.geography.fake.FakePlace
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.ints.shouldBeNegative
import io.kotest.matchers.ints.shouldBePositive
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import kotlinx.datetime.Instant
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import okio.use
import kotlin.test.AfterTest
import kotlin.test.Test

class FilesystemMomentTest {

    private val fileSystem = FakeFileSystem()
    private val places = FilesystemMemorablePlaces(fileSystem, "content/places".toPath())
    private val stories = FilesystemStories(fileSystem, "content".toPath(), places)
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
        moment.place.shouldBe(NullIsland)
    }

    @Test
    fun `Writes details updates to the filesystem`() {
        val moment = moments.new()

        moment.update(timestamp = Timestamp(Instant.fromEpochMilliseconds(1000)))
        moment.update(description = TokenableString("Foo"))
        moment.update(sentiment = Sentiment(0.5F))

        val path = "content/${story.id}/moments/${moment.id}".toPath()
        val fileContent = fileSystem.source(path).buffer().use { it.readUtf8() }
        fileContent.shouldContain("Foo")
        fileContent.shouldContain("0.5")
        moment.timestamp.shouldBe(Timestamp(Instant.fromEpochMilliseconds(1000)))
        moment.description.shouldBe(TokenableString("Foo"))
        moment.sentiment.shouldBe(Sentiment(0.5F))
    }

    @Test
    fun `Writes place updates to the filesystem`() {
        val moment = moments.new()
        val firstPlace = FakePlace()
        val secondPlace = FakePlace()
        val firstPlaceFileContent = {
            val path = "content/places/${firstPlace.id}".toPath()
            fileSystem.source(path).buffer().use { it.readUtf8() }
        }
        val secondPlaceFileContent = {
            val path = "content/places/${secondPlace.id}".toPath()
            fileSystem.source(path).buffer().use { it.readUtf8() }
        }

        moment.update(place = firstPlace)

        firstPlaceFileContent().shouldContain(moment.id.toString())
        moment.place.id.shouldBe(firstPlace.id)

        moment.update(place = secondPlace)

        firstPlaceFileContent().shouldNotContain(moment.id.toString())
        secondPlaceFileContent().shouldContain(moment.id.toString())
        moment.place.id.shouldBe(secondPlace.id)

        moment.update(place = NullIsland)

        firstPlaceFileContent().shouldNotContain(moment.id.toString())
        secondPlaceFileContent().shouldNotContain(moment.id.toString())
        moment.place.id.shouldBe(NullIsland.id)

        moment.update(place = firstPlace)

        firstPlaceFileContent().shouldContain(moment.id.toString())
        secondPlaceFileContent().shouldNotContain(moment.id.toString())
        moment.place.id.shouldBe(firstPlace.id)
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
