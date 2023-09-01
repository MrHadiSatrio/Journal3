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

package com.hadisatrio.libs.kotlin.json

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonPrimitive
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.AfterTest
import kotlin.test.Test

class JsonFileTest {

    private val fileSystem = FakeFileSystem()
    private val path = "foo.json".toPath()
    private val jsonFile = JsonFile(fileSystem, path)

    @AfterTest
    fun `Closes all file streams`() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `Returns the underlying file name as its name`() {
        jsonFile.name.shouldBe("foo.json")
    }

    @Test
    fun `Writes elements to the file`() {
        jsonFile.put("Fizz", JsonPrimitive("Buzz"))

        val fileContent = fileSystem.source(path).buffer().use { it.readUtf8() }
        fileContent.shouldBe("""{"Fizz":"Buzz"}""")
    }

    @Test
    fun `Gets values stored in the JSON according to a given key`() {
        jsonFile.put("Fizz", JsonPrimitive("Buzz"))

        jsonFile.get("Fizz").shouldBe(JsonPrimitive("Buzz"))
        jsonFile.getRaw("Fizz").shouldBe("Buzz")
        jsonFile.get("Foo").shouldBeNull()
        jsonFile.getRaw("Foo").shouldBeNull()
    }

    @Test
    fun `Tells correctly whether or not it exists`() {
        jsonFile.exists().shouldBeFalse()
        jsonFile.put("Fizz", JsonPrimitive("Buzz"))
        jsonFile.exists().shouldBeTrue()
    }

    @Test
    fun `Deletes itself from the filesystem`() {
        jsonFile.put("Fizz", JsonPrimitive("Buzz"))

        fileSystem.exists(path).shouldBeTrue()
        jsonFile.delete()
        fileSystem.exists(path).shouldBeFalse()
    }

    @Test
    fun `Guards against illegal JSON characters during writes`() = shouldNotThrow<Exception> {
        jsonFile.put("Fizz", JsonPrimitive(""" Buzz" 👩‍👩‍👧 """))

        val fileContent = fileSystem.source(path).buffer().use { it.readUtf8() }
        fileContent.shouldBe("""{"Fizz":" Buzz\" 👩‍👩‍👧 "}""")
    }

    @Test
    fun `Guards against illegal JSON characters during reads`() = shouldNotThrow<Exception> {
        jsonFile.put("Fizz", JsonPrimitive(""" Buzz" 👩‍👩‍👧 """))

        jsonFile.get("Fizz").shouldBe(JsonPrimitive(""" Buzz" 👩‍👩‍👧 """))
        jsonFile.getRaw("Fizz").shouldBe(""" Buzz" 👩‍👩‍👧 """)
    }
}
