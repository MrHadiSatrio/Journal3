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

package com.hadisatrio.apps.kotlin.journal3.sentiment

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.mockk
import kotlin.test.Test

class InitDeferringSentimentAnalystTest {

    @Test
    fun `Defers the initialization of its origin`() {
        var isInitialized = false
        val origin = {
            isInitialized = true
            mockk<SentimentAnalyst>(relaxed = true)
        }
        val deferred = InitDeferringSentimentAnalyst(origin)

        isInitialized.shouldBeFalse()
        deferred.train(emptyMap())
        isInitialized.shouldBeTrue()
    }
}
