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

package com.hadisatrio.apps.kotlin.journal3.datetime

import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import kotlin.test.Test

class LiteralTimestampTest {

    @Test
    fun `Treats two identical timestamps as equal`() {
        val one = LiteralTimestamp(0)
        val another = LiteralTimestamp("1970-01-01T00:00:00Z")
        one.shouldBeEqual(another)
        one.hashCode().shouldBeEqual(another.hashCode())
    }

    @Test
    fun `Treats two differing timestamps as non equal`() {
        val one = LiteralTimestamp(10)
        val another = LiteralTimestamp("1970-01-01T00:00:00Z")
        one.shouldNotBeEqual(another)
        one.hashCode().shouldNotBeEqual(another.hashCode())
    }
}
