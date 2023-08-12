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

class SentimentAnalyzingMoment(
    private val analyst: SentimentAnalyst,
    private val origin: MomentInEdit
) : MomentInEdit by origin {

    private var isSentimentOverridden: Boolean = false

    override fun update(description: TokenableString) {
        origin.update(description)
        if (isSentimentOverridden) return
        val sentiment = analyst.analyze(description.toString())
        origin.update(sentiment)
    }

    override fun update(sentiment: Sentiment) {
        isSentimentOverridden = true
        origin.update(sentiment)
    }

    override fun commit() {
        if (isSentimentOverridden) analyst.train(setOf(this))
        origin.commit()
    }
}
