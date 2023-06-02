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
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okio.Source
import kotlin.test.BeforeTest
import kotlin.test.Test

class SchemeWiseSourcesTest {

    private val fooScheme = "foo"
    private val fooSource = mockk<Sources>()
    private val barScheme = "bar"
    private val barSource = mockk<Sources>()
    private val sources = SchemeWiseSources(
        fooScheme to fooSource,
        barScheme to barSource
    )

    @BeforeTest
    fun `Init mocks`() {
        every { fooSource.open(any()) } returns mockk()
        every { barSource.open(any()) } returns mockk()
    }

    @Test
    fun `Opens URI with a known scheme`() {
        sources.open(Uri.fromString("foo://foo")).shouldBeInstanceOf<Source>()
        sources.open(Uri.fromString("bar://bar")).shouldBeInstanceOf<Source>()
        verify(exactly = 1) { fooSource.open(any()) }
        verify(exactly = 1) { barSource.open(any()) }
    }

    @Test
    fun `Throws IllegalArgumentException given URI with an unknown scheme`() {
        shouldThrow<IllegalArgumentException> {
            sources.open(Uri.fromString("http://foo.com"))
        }
    }
}
