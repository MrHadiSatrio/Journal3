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

package com.hadisatrio.libs.kotlin.io

import com.chrynan.uri.core.Uri
import com.chrynan.uri.core.fromString
import com.hadisatrio.libs.kotlin.io.filesystem.FileSystemSources
import com.hadisatrio.libs.kotlin.io.uri.toUri
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.AfterTest
import kotlin.test.Test

class FileSystemSourcesTest {

    private val fileSystem = FakeFileSystem()
    private val sources = FileSystemSources(fileSystem)

    private val arbitraryExternalFilePath: Path by lazy {
        "foo".toPath().apply {
            fileSystem.sink(this).buffer().use { it.writeUtf8("foo") }
        }
    }

    @AfterTest
    fun `Closes all file streams`() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `Opens an input stream given a valid URI`() {
        val path = arbitraryExternalFilePath
        val uri = path.toUri()

        sources.open(uri).buffer().use { source ->
            source.readUtf8().shouldBe("foo")
        }
    }

    @Test
    fun `Throws when asked to open a stream for a non-supported URI`() {
        listOf(
            Uri.fromString("content://foo"),
            Uri.fromString("http://foo"),
            Uri.fromString("https://foo"),
            Uri.fromString("ftp://foo")
        ).forEach { uri ->
            shouldThrow<IllegalArgumentException> { sources.open(uri) }
        }
    }
}
