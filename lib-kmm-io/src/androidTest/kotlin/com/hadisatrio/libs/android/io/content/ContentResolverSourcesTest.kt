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

package com.hadisatrio.libs.android.io.content

import android.content.ContentResolver
import androidx.test.runner.AndroidJUnit4
import com.chrynan.uri.core.Uri
import com.chrynan.uri.core.fromString
import com.hadisatrio.libs.android.io.uri.toAndroidUri
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import okio.FileNotFoundException
import okio.buffer
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.IllegalArgumentException

@RunWith(AndroidJUnit4::class)
class ContentResolverSourcesTest {

    private val contentResolver = mockk<ContentResolver>()
    private val sources = ContentResolverSources(contentResolver)
    private val validUri = Uri.fromString("content://foo")
    private val content = "foo"

    @Before
    fun `Init mocks`() {
        every { contentResolver.openInputStream(any()) }
            .returns(null)
        every { contentResolver.openInputStream(validUri.toAndroidUri()) }
            .returns(content.byteInputStream())
    }

    @Test
    fun `Opens an input stream given a valid URI`() {
        sources.open(validUri).buffer().use { source ->
            source.readUtf8().shouldBe(content)
        }
    }

    @Test
    fun `Throws when asked to open a stream for a non-existent content`() {
        val uri = Uri.fromString("content://bar")
        shouldThrow<FileNotFoundException> { sources.open(uri) }
    }

    @Test
    fun `Throws when asked to open a stream for a non-supported URI`() {
        listOf(
            Uri.fromString("file://foo"),
            Uri.fromString("http://foo"),
            Uri.fromString("https://foo"),
            Uri.fromString("ftp://foo")
        ).forEach { uri ->
            shouldThrow<IllegalArgumentException> { sources.open(uri) }
        }
    }
}
