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

package com.hadisatrio.apps.kotlin.journal3.moment

import com.hadisatrio.apps.kotlin.journal3.sentiment.NegativeishSentimentRange
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.story.SelfPopulatingStories
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.test.BeforeTest
import kotlin.test.Test

class SentimentRangedMomentsTest {

    private val origin: Moments = SelfPopulatingStories(noOfStories = 1, noOfMoments = 10, FakeStories()).moments
    private val moments: SentimentRangedMoments = SentimentRangedMoments(NegativeishSentimentRange, origin)

    @BeforeTest
    fun `Init moments`() {
        origin.forEachIndexed { index, moment ->
            if (index % 2 == 0) {
                moment.update(Sentiment(0.0F))
            } else {
                moment.update(Sentiment(1.0F))
            }
        }
    }

    @Test
    fun `Filters out moments whose sentiment is out of range`() {
        val sentimentalIds = moments.map { it.id }.toList()
        sentimentalIds.forEach { id ->
            val moment = origin.find(id).first()
            (moment.sentiment in NegativeishSentimentRange).shouldBeTrue()
        }
    }
}
