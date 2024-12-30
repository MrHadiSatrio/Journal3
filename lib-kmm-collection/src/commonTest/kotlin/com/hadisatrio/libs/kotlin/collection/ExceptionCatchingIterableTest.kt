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

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test

class ExceptionCatchingIterableTest {

    @Test
    fun `Delegates to the origin`() {
        val list = listOf(1, 2, 3)
        val iterable = ExceptionCatchingIterable({ }, list)

        val iterator = iterable.iterator()

        iterator.hasNext().shouldBeTrue()
        iterator.next() shouldBe(1)
        iterator.hasNext().shouldBeTrue()
        iterator.next() shouldBe(2)
        iterator.hasNext().shouldBeTrue()
        iterator.next() shouldBe(3)
        iterator.hasNext().shouldBeFalse()
    }

    @Test
    fun `Catches exceptions in the origin's hasNext()`() {
        var exceptionCaught = false
        val iterator = mockk<Iterator<Int>>()
        every { iterator.hasNext() } throws RuntimeException("Boom!")

        val iterable = ExceptionCatchingIterable({ exceptionCaught = true }, IteratorIterable { iterator })
        val catchingIterator = iterable.iterator()

        catchingIterator.hasNext().shouldBeFalse()
        exceptionCaught.shouldBeTrue()
    }
}
