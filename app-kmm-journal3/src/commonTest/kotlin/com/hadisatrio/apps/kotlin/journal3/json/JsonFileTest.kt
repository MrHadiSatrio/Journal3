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

package com.hadisatrio.apps.kotlin.journal3.json

import io.kotest.assertions.throwables.shouldNotThrow
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

    @AfterTest
    fun `Closes all file streams`() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `Writes elements to the file`() {
        val jsonFile = JsonFile(fileSystem, path)
        jsonFile.put("Fizz", JsonPrimitive("Buzz"))

        val fileContent = fileSystem.source(path).buffer().use { it.readUtf8() }
        fileContent.shouldBe("""{"Fizz":"Buzz"}""")
    }

    @Test
    fun `Guards against illegal JSON characters during writes`() = shouldNotThrow<Exception> {
        val jsonFile = JsonFile(fileSystem, path)
        jsonFile.put("Fizz", JsonPrimitive(""" Buzz" 👩‍👩‍👧 """))

        val fileContent = fileSystem.source(path).buffer().use { it.readUtf8() }
        fileContent.shouldBe("""{"Fizz":" Buzz\" 👩‍👩‍👧 "}""")
    }

    @Test
    fun `Guards against illegal JSON characters during reads`() = shouldNotThrow<Exception> {
        val jsonFile = JsonFile(fileSystem, path)
        jsonFile.put("Fizz", JsonPrimitive(""" Buzz" 👩‍👩‍👧 """))

        jsonFile.get("Fizz").shouldBe(JsonPrimitive(""" Buzz" 👩‍👩‍👧 """))
        jsonFile.getRaw("Fizz").shouldBe(""" Buzz" 👩‍👩‍👧 """)
    }
}
