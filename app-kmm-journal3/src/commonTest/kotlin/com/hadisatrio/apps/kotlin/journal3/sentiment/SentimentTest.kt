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

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeNegative
import io.kotest.matchers.ints.shouldBePositive
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class SentimentTest {

    @Test
    fun `Throws when instantiated with an invalid value`() {
        shouldThrow<IllegalArgumentException> { Sentiment(-0.1F) }
        shouldThrow<IllegalArgumentException> { Sentiment(1.1F) }

        shouldNotThrow<IllegalArgumentException> { Sentiment(0.5F) }
    }

    @Test
    fun `Infers correct value when instantiated with multiple values`() {
        val rawSentiments = listOf(
            Sentiment(1.0F),
            Sentiment(0.75F),
            Sentiment(0.5F),
            Sentiment(0.25F),
            Sentiment(0.0F)
        )

        val sentiment = Sentiment(rawSentiments)

        val averageValue = rawSentiments.map { it.value }.average()
        sentiment.value.shouldBe(averageValue)
    }

    @Test
    fun `Compares itself to other sentiment on basis of value`() {
        Sentiment(1.0F).compareTo(Sentiment(0.5F)).shouldBePositive()
        Sentiment(1.0F).compareTo(Sentiment(1.0F)).shouldBeZero()
        Sentiment(0.5F).compareTo(Sentiment(1.0F)).shouldBeNegative()
    }
}
