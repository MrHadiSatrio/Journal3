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
import com.hadisatrio.libs.kotlin.geography.LiteralCoordinates
import com.hadisatrio.libs.kotlin.geography.fake.FakePlace
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import okio.use
import kotlin.test.AfterTest
import kotlin.test.Test

class FilesystemMemorablePlaceTest {

    private val fileSystem = FakeFileSystem()
    private val places = FilesystemMemorablePlaces(fileSystem, "content".toPath())
    private val place = FakePlace()
    private val memorablePlace = places.remember(place)

    @AfterTest
    fun `Closes all file streams`() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `Write updates to the filesystem`() {
        memorablePlace.updateLabel("Foo")
        memorablePlace.updateName("Bar")
        memorablePlace.updateAddress("FizzBuzz")
        memorablePlace.update(LiteralCoordinates("-7.607355,110.203804"))

        val path = "content/${place.id}".toPath()
        val fileContent = fileSystem.source(path).buffer().use { it.readUtf8() }
        fileContent.contains("Foo")
        fileContent.contains("Bar")
        fileContent.contains("FizzBuzz")
        fileContent.contains("-7.607355,110.203804")
        memorablePlace.label.shouldBe("Foo")
        memorablePlace.name.shouldBe("Bar")
        memorablePlace.address.shouldBe("FizzBuzz")
        memorablePlace.coordinates.toString().shouldBe("-7.607355,110.203804")
    }

    @Test
    fun `Reports relevancy for moments`() {
        val oneMomentId = uuid4()
        val otherMomentId = uuid4()
        val momentId = uuid4()

        memorablePlace.link(oneMomentId)
        memorablePlace.link(otherMomentId)
        memorablePlace.unlink(otherMomentId)

        memorablePlace.relevantTo(oneMomentId).shouldBeTrue()
        memorablePlace.relevantTo(otherMomentId).shouldBeFalse()
        memorablePlace.relevantTo(momentId).shouldBeFalse()
    }
}
