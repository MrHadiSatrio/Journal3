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

package com.hadisatrio.apps.kotlin.journal3.token

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class TokenableStringTest {

    @Test
    fun `Tokenizes mentions from a given string`() {
        val tokenableString = TokenableString("Foo, @Bar, Fizz, @Buzz")

        tokenableString.tokens().shouldHaveSize(2)
        tokenableString.tokens().map { it.toString() }.shouldContain("@Bar")
        tokenableString.tokens().map { it.toString() }.shouldContain("@Buzz")
    }

    @Test
    fun `Represents itself as string through the raw string`() {
        val rawString = "Foo, @Bar, Fizz, @Buzz"

        TokenableString(rawString).toString().shouldBe(rawString)
    }
}
