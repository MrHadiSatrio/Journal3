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

package com.hadisatrio.apps.kotlin.journal3.story.filesystem

import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMemorablePlaces
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import okio.use
import kotlin.test.AfterTest
import kotlin.test.Test

class FilesystemStoryTest {

    private val fileSystem = FakeFileSystem()
    private val places = FilesystemMemorablePlaces(fileSystem, "content/places".toPath())
    private val stories = FilesystemStories(fileSystem, "content".toPath(), places)

    @AfterTest
    fun `Closes all file streams`() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `Returns expected default values`() {
        val story = stories.new()

        story.title.shouldBe("")
        story.synopsis.shouldBe(TokenableString(""))
    }

    @Test
    fun `Write updates to the filesystem`() {
        val story = stories.new()

        story.update(title = "Foo")
        story.update(synopsis = TokenableString("Bar"))

        val path = "content/${story.id}/details".toPath()
        val fileContent = fileSystem.source(path).buffer().use { it.readUtf8() }
        fileContent.contains("Foo")
        fileContent.contains("Bar")
        story.title.shouldBe("Foo")
        story.synopsis.shouldBe(TokenableString("Bar"))
    }

    @Test
    fun `Deletes itself from the filesystem`() {
        val story = stories.new()

        story.moments.new()
        story.forget()

        val path = "content/${story.id}".toPath()
        fileSystem.exists(path).shouldBeFalse()
    }
}
