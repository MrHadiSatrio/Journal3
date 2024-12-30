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

package com.hadisatrio.libs.kotlin.collection

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class PagingIteratorTest {

    @Test
    fun `Reports hasNext() as false when source is empty`() {
        val source = FakeSource(emptyList<Int>())

        val iterator = PagingIterator(source)

        iterator.hasNext().shouldBeFalse()
        shouldThrow<NoSuchElementException> { iterator.next() }
    }

    @Test
    fun `Handles single-page source correctly`() {
        val items = listOf(1, 2, 3)
        val source = FakeSource(items)

        val iterator = PagingIterator(source)

        iterator.hasNext().shouldBeTrue()
        iterator.next().shouldBe(1)
        iterator.hasNext().shouldBeTrue()
        iterator.next().shouldBe(2)
        iterator.hasNext().shouldBeTrue()
        iterator.next().shouldBe(3)
        iterator.hasNext().shouldBeFalse()
    }

    @Test
    fun `Handles multi-page source correctly`() {
        val firstPage = listOf(1, 2, 3)
        val secondPage = listOf(4, 5, 6)
        val source = FakeSource(firstPage, secondPage)

        val iterator = PagingIterator(source)

        iterator.hasNext().shouldBeTrue()
        iterator.next().shouldBe(1)
        iterator.hasNext().shouldBeTrue()
        iterator.next().shouldBe(2)
        iterator.hasNext().shouldBeTrue()
        iterator.next().shouldBe(3)
        iterator.hasNext().shouldBeTrue()
        iterator.next().shouldBe(4)
        iterator.hasNext().shouldBeTrue()
        iterator.next().shouldBe(5)
        iterator.hasNext().shouldBeTrue()
        iterator.next().shouldBe(6)
        iterator.hasNext().shouldBeFalse()
    }

    private class FakeSource<T>(vararg pages: List<T>) : PagingIterator.Source<T> {
        private val pages: List<List<T>> = pages.toList()

        override fun obtain(page: Int, pageSize: Int): PagingIterator.Page<T> {
            val items = if (page < pages.size) pages[page] else emptyList()
            return FakePage(items, page < pages.size - 1)
        }
    }

    private class FakePage<T>(
        override val items: List<T>,
        override val hasNext: Boolean
    ) : PagingIterator.Page<T>
}
