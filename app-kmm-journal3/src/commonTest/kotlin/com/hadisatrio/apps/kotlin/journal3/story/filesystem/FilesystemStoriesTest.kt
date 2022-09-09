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

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.AfterTest
import kotlin.test.Test

class FilesystemStoriesTest {

    private val fileSystem = FakeFileSystem()
    private val stories = FilesystemStories(fileSystem, "content".toPath())

    @AfterTest
    fun `Closes all file streams`() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `Writes new stories to the filesystem`() {
        val story = stories.new()

        story.update("Foo")

        stories.shouldHaveSize(1)
        story.title.shouldBe("Foo")
        fileSystem.metadata("content/${story.id}".toPath()).isDirectory.shouldBeTrue()
    }
}
