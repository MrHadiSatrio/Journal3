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

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.chrynan.uri.core.Uri
import com.hadisatrio.apps.kotlin.journal3.moment.MemorableFile
import com.hadisatrio.libs.kotlin.io.filesystem.FileSystemSources
import com.hadisatrio.libs.kotlin.io.uri.toUri
import com.hadisatrio.libs.kotlin.json.JsonFile
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonPrimitive
import okio.FileNotFoundException
import okio.Path
import okio.Path.Companion.toPath
import okio.Source
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class FilesystemMemorableFilesTest {

    private val fileSystem = FakeFileSystem()
    private val externalDirPath = "external/".toPath()
    private val internalDirPath = "internal/".toPath()
    private val sources = FileSystemSources(fileSystem)
    private val files = FilesystemMemorableFiles(sources, fileSystem, internalDirPath)

    private val arbitraryExternalFilePath: Path by lazy {
        (externalDirPath / "foo").apply {
            JsonFile(fileSystem, this).put("foo", JsonPrimitive("bar"))
        }
    }
    private val arbitraryExternalFileUri: Uri by lazy {
        arbitraryExternalFilePath.toUri()
    }

    @BeforeTest
    fun `Initializes directories`() {
        listOf(externalDirPath, internalDirPath).forEach { path ->
            fileSystem.createDirectories(path, mustCreate = true)
        }
    }

    @AfterTest
    fun `Closes all file streams`() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `Establishes relation to a new path`() {
        val momentId = uuid4()

        files.relate(momentId, arbitraryExternalFileUri)

        val internalFile = files.first() as MemorableFile
        val internalFileSource = fileSystem.source(internalFile.uri.path.toPath())
        val externalFileSource = fileSystem.source(arbitraryExternalFilePath)
        files.count().shouldBe(1)
        internalFileSource.contentsEquals(externalFileSource)
        files.relevantTo(momentId).shouldHaveSize(1)
        files.relevantTo(uuid4()).shouldBeEmpty()
    }

    @Test
    fun `Establishes relation to an existing path having equivalent content when it exists`() {
        val momentIds = mutableSetOf<Uuid>()
        repeat(10) {
            val momentId = uuid4()
            momentIds += momentId
            files.relate(momentId, arbitraryExternalFileUri)
        }

        files.count().shouldBe(1)
        momentIds.forEach { files.relevantTo(it).shouldHaveSize(1) }
    }

    @Test
    fun `Finds files by its ID`() {
        files.relate(uuid4(), arbitraryExternalFileUri)

        val internalFile = files.first() as MemorableFile
        files.find(internalFile.id).shouldHaveSize(1)
        files.find(uuid4()).shouldBeEmpty()
    }

    @Test
    fun `Throws when asked to establish relation to an unknown object`() {
        listOf<Any>(
            "raw/path/string",
            Int.MAX_VALUE,
            Long.MAX_VALUE
        ).forEach { thing ->
            shouldThrow<IllegalArgumentException> { files.relate(uuid4(), thing) }
        }
    }

    @Test
    fun `Throws when asked to relate paths that doesn't actually exists`() {
        shouldThrow<FileNotFoundException> {
            files.relate(uuid4(), (externalDirPath / "foo").toUri())
        }
    }

    private fun Source.contentsEquals(other: Source): Boolean {
        val thisHash = this.buffer().use { it.readByteString().sha256() }
        val otherHash = other.buffer().use { it.readByteString().sha256() }
        return thisHash == otherHash
    }
}
