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

@file:Suppress("MagicNumber")

package com.hadisatrio.apps.kotlin.journal3.sentiment

object VeryPositiveSentimentRange : ClosedRange<Sentiment> {
    override val endInclusive: Sentiment = Sentiment(1.0F)
    override val start: Sentiment = Sentiment(0.81F)
}

object PositiveSentimentRange : ClosedRange<Sentiment> {
    override val endInclusive: Sentiment = Sentiment(0.80F)
    override val start: Sentiment = Sentiment(0.56F)
}

object NeutralSentimentRange : ClosedRange<Sentiment> {
    override val endInclusive: Sentiment = Sentiment(0.55F)
    override val start: Sentiment = Sentiment(0.45F)
}

object NegativeSentimentRange : ClosedRange<Sentiment> {
    override val endInclusive: Sentiment = Sentiment(0.44F)
    override val start: Sentiment = Sentiment(0.20F)
}

object VeryNegativeSentimentRange : ClosedRange<Sentiment> {
    override val endInclusive: Sentiment = Sentiment(0.19F)
    override val start: Sentiment = Sentiment(0.0F)
}

object PositiveishSentimentRange : ClosedRange<Sentiment> {
    override val endInclusive: Sentiment = Sentiment(1.0F)
    override val start: Sentiment = Sentiment(0.61F)
}

object NegativeishSentimentRange : ClosedRange<Sentiment> {
    override val endInclusive: Sentiment = Sentiment(0.39F)
    override val start: Sentiment = Sentiment(0.0F)
}
