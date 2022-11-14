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
import com.hadisatrio.apps.kotlin.journal3.geography.FakePlace
import com.hadisatrio.apps.kotlin.journal3.moment.FakeMoments
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.AfterTest
import kotlin.test.Test

class FilesystemMomentfulPlacesTest {

    private val fileSystem = FakeFileSystem()

    @AfterTest
    fun `Closes all file streams`() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `Writes newly remembered places to disk`() {
        val places = FilesystemMomentfulPlaces(fileSystem, "content".toPath())
        val place = FakePlace()

        val remembered = places.remember(place)

        val path = "content/${place.id}".toPath()
        fileSystem.exists(path).shouldBeTrue()
        remembered.id.shouldBe(place.id)
        remembered.label.shouldBe(place.name) // Labels should default to name for newly-remembered places.
        remembered.name.shouldBe(place.name)
        remembered.address.shouldBe(place.address)
        remembered.coordinates.shouldBe(place.coordinates)
    }

    @Test
    fun `Doesn't change anything if asked to remember an existing place`() {
        val places = FilesystemMomentfulPlaces(fileSystem, "content".toPath())
        val place = FakePlace()

        val old = places.remember(place)
        old.updateLabel("Foo")
        val new = places.remember(place)

        new.label.shouldBe("Foo")
    }

    @Test
    fun `Finds a place by its ID`() {
        val places = FilesystemMomentfulPlaces(fileSystem, "content".toPath())
        repeat(10) { places.remember(FakePlace()) }
        val randomized = places.toList().random()

        val found = places.find(randomized.id)

        found.shouldHaveSize(1)
    }

    @Test
    fun `Returns empty iterable when asked to find a non-existent place by ID`() {
        val places = FilesystemMomentfulPlaces(fileSystem, "content".toPath())
        repeat(10) { places.remember(FakePlace()) }

        val found = places.find(uuid4())

        found.shouldBeEmpty()
    }

    @Test
    fun `Finds a place relevant to given moment`() {
        val moment = FakeMoments().new()
        val places = FilesystemMomentfulPlaces(fileSystem, "content".toPath())
        repeat(10) { places.remember(FakePlace()) }
        val randomized = places.toList().random()
        randomized.link(moment)

        val found = places.relevantTo(moment)

        found.shouldHaveSize(1)
    }

    @Test
    fun `Returns empty iterable when asked to find a place relevant to an unknown moment`() {
        val moment = FakeMoments().new()
        val places = FilesystemMomentfulPlaces(fileSystem, "content".toPath())
        repeat(10) { places.remember(FakePlace()).link(moment) }

        val found = places.relevantTo(FakeMoments().new())

        found.shouldBeEmpty()
    }
}
