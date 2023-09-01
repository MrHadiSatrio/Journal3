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

import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.sentiment.SentimentAnalyst
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class SentimentAnalyzingMomentTest {

    private val analyst = mockk<SentimentAnalyst>(relaxed = true)
    private val origin = mockk<MomentInEdit>(relaxed = true)
    private val moment = SentimentAnalyzingMoment(analyst, origin)

    @Test
    fun `Consults the analyst upon updates to its description, unless the user overrides`() {
        moment.update(TokenableString("Foo"))
        verify { origin.update(TokenableString("Foo")) }

        moment.update(Sentiment(0.5F))
        moment.update(TokenableString("Fizz"))
        verify { origin.update(TokenableString("Fizz")) }

        verify(exactly = 1) { analyst.analyze(any()) }
    }

    @Test
    fun `Trains the analyst when the user overrides the sentiment`() {
        moment.update(Sentiment(0.5F))
        moment.commit()
        verify(exactly = 1) { analyst.train(mapOf(moment.description.toString() to moment.sentiment)) }
    }

    @Test
    fun `Refrains from training the analyst when user does not override the sentiment`() {
        moment.commit()
        verify(inverse = true) { analyst.train(any()) }
    }
}
