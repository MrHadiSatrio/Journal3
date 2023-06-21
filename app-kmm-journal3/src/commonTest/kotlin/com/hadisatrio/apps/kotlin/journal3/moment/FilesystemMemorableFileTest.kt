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

package com.hadisatrio.apps.kotlin.journal3.moment

import com.benasher44.uuid.uuid4
import com.chrynan.uri.core.Uri
import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMemorableFile
import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMemorableFiles
import com.hadisatrio.libs.kotlin.io.filesystem.FileSystemSources
import com.hadisatrio.libs.kotlin.io.uri.toUri
import com.hadisatrio.libs.kotlin.json.JsonFile
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.serialization.json.JsonPrimitive
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import okio.use
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class FilesystemMemorableFileTest {

    private val fileSystem = FakeFileSystem()
    private val externalDirPath = "external/".toPath()
    private val internalDirPath = "internal/".toPath()
    private val sources = FileSystemSources(fileSystem)
    private val files = FilesystemMemorableFiles(sources, fileSystem, internalDirPath)
    private val momentId = uuid4()

    private lateinit var externalPath: Path
    private lateinit var externalUri: Uri
    private lateinit var attributionPath: Path
    private lateinit var file: FilesystemMemorableFile

    @BeforeTest
    fun `Initializes directories & sets up test subject`() {
        listOf(externalDirPath, internalDirPath).forEach { path ->
            fileSystem.createDirectories(path, mustCreate = true)
        }

        externalPath = externalDirPath / "foo"
        externalUri = externalPath.toUri()
        val externalFile = JsonFile(fileSystem, externalPath)
        externalFile.put("foo", JsonPrimitive("bar"))

        files.relate(momentId, externalUri)

        file = files.first() as FilesystemMemorableFile
        attributionPath = internalDirPath.resolve(".attr_${file.id}")
    }

    @AfterTest
    fun `Closes all file streams`() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `Writes attribution updates to the filesystem`() {
        val momentId = uuid4()
        files.relate(momentId, externalUri)
        val attrFileContent = { fileSystem.source(attributionPath).buffer().use { it.readUtf8() } }

        fileSystem.exists(externalPath).shouldBeTrue()
        fileSystem.exists(attributionPath).shouldBeTrue()

        attrFileContent().shouldContain(momentId.toString())
    }

    @Test
    fun `Establishes relation by basing on the moment ID and the file`() {
        val oneMomentId = uuid4()
        val otherMomentId = uuid4()

        files.relate(oneMomentId, externalUri)
        files.shouldHaveSize(1)
        files.relevantTo(oneMomentId).shouldHaveSize(1)

        files.relate(oneMomentId, externalUri)
        files.shouldHaveSize(1)
        files.relevantTo(oneMomentId).shouldHaveSize(1)

        files.relate(otherMomentId, externalUri)
        files.shouldHaveSize(1)
        files.relevantTo(oneMomentId).shouldHaveSize(1)
        files.relevantTo(otherMomentId).shouldHaveSize(1)

        file.unlink(momentId)
        file.unlink(oneMomentId)
        file.unlink(otherMomentId)
        files.shouldBeEmpty()
    }

    @Test
    fun `Produces a valid URI of itself`() {
        file.uri.uriString.shouldBe("file:///$internalDirPath/${file.id}")
    }
}
