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

package com.hadisatrio.libs.kotlin.frontmatter

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import okio.use
import kotlin.test.AfterTest
import kotlin.test.Test

class FrontmatterFileTest {

    private val fileSystem = FakeFileSystem()
    private val path = "foo.md".toPath()
    private val frontmatterFile = FrontmatterFile(fileSystem, path)

    @AfterTest
    fun `Closes all file streams`() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `Returns the underlying file name as its name`() {
        frontmatterFile.name.shouldBe("foo.md")
    }

    @Test
    fun `Round-trips frontmatter key-value pairs via put and get`() {
        frontmatterFile.put("sentiment", "0.85")
        frontmatterFile.put("is_notable", "true")

        frontmatterFile.get("sentiment").shouldBe("0.85")
        frontmatterFile.get("is_notable").shouldBe("true")
    }

    @Test
    fun `Returns null for non-existent key`() {
        frontmatterFile.get("missing").shouldBeNull()
    }

    @Test
    fun `Handles colons in values (ISO-8601 timestamps)`() {
        frontmatterFile.put("timestamp", "2024-02-25T15:30:00Z")

        frontmatterFile.get("timestamp").shouldBe("2024-02-25T15:30:00Z")
    }

    @Test
    fun `Writes frontmatter delimiters and key-value lines to file`() {
        frontmatterFile.put("sentiment", "0.85")

        val fileContent = fileSystem.source(path).buffer().use { it.readUtf8() }
        fileContent.shouldContain("---")
        fileContent.shouldContain("sentiment: 0.85")
    }

    @Test
    fun `Preserves existing frontmatter when adding another key`() {
        frontmatterFile.put("sentiment", "0.85")
        frontmatterFile.put("is_notable", "true")

        frontmatterFile.get("sentiment").shouldBe("0.85")
        frontmatterFile.get("is_notable").shouldBe("true")
    }

    @Test
    fun `Round-trips markdown body via updateBody and body`() {
        frontmatterFile.updateBody("Had a great day!")

        frontmatterFile.body().shouldBe("Had a great day!")
    }

    @Test
    fun `Returns empty string for body when file does not exist`() {
        frontmatterFile.body().shouldBe("")
    }

    @Test
    fun `Preserves frontmatter when updating body`() {
        frontmatterFile.put("sentiment", "0.85")
        frontmatterFile.updateBody("Great day!")

        frontmatterFile.get("sentiment").shouldBe("0.85")
        frontmatterFile.body().shouldBe("Great day!")
    }

    @Test
    fun `Preserves body when updating frontmatter`() {
        frontmatterFile.updateBody("Great day!")
        frontmatterFile.put("sentiment", "0.85")

        frontmatterFile.body().shouldBe("Great day!")
        frontmatterFile.get("sentiment").shouldBe("0.85")
    }

    @Test
    fun `Preserves newlines in body (multi-paragraph descriptions)`() {
        val multiParagraph = "First paragraph.\n\nSecond paragraph."
        frontmatterFile.updateBody(multiParagraph)

        frontmatterFile.body().shouldBe(multiParagraph)
    }
}
