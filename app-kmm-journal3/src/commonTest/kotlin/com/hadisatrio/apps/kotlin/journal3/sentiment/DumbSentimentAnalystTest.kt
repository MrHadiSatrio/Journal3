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

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DumbSentimentAnalystTest {

    @Test
    fun `Dumbly infer a given positive string as with the highest sentiment value`() {
        DumbSentimentAnalyst.analyze("I'm so happy!").value.shouldBe(1.0F)
    }

    @Test
    fun `Dumbly infer a given negative string as with the lowest sentiment value`() {
        DumbSentimentAnalyst.analyze("I'm so exhausted!").value.shouldBe(0.0F)
    }

    @Test
    fun `Dumbly infer an string as with the default sentiment value`() {
        DumbSentimentAnalyst.analyze("").value.shouldBe(0.123456789F)
    }
}
