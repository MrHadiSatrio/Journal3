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

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldHaveSameHashCodeAs
import io.kotest.matchers.types.shouldNotHaveSameHashCodeAs
import kotlin.test.Test

class TokenTest {

    @Test
    fun `Throws when instantiated with an invalid string`() {
        shouldThrow<IllegalArgumentException> { Token("") }
        shouldThrow<IllegalArgumentException> { Token("NoPrefix") }
        shouldThrow<IllegalArgumentException> { Token("&UnsupportedPrefix") }
        shouldThrow<IllegalArgumentException> { Token("@Contains Spaces") }
        shouldThrow<IllegalArgumentException> { Token("#Contains Spaces") }

        shouldNotThrow<Throwable> { Token("@mention") }
        shouldNotThrow<Throwable> { Token("#hashtag") }
    }

    @Test
    fun `Represents itself as string through the raw string`() {
        val rawToken = "@mention"

        Token(rawToken).toString().shouldBe(rawToken)
    }

    @Test
    fun `Checks for equality based the type and the string value`() {
        Token("@mention").shouldBe(Token("@mention"))
        Token("@mention").shouldHaveSameHashCodeAs(Token("@mention"))
        Token("@mention").shouldNotBe(Token("#hashcode"))
        Token("@mention").shouldNotHaveSameHashCodeAs(Token("#hashcode"))
        Token("@mention").shouldNotBe("@mention")
    }
}
