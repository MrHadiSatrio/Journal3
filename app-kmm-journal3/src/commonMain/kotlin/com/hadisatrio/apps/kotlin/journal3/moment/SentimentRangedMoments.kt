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

import com.benasher44.uuid.Uuid
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment

class SentimentRangedMoments(
    private val sentimentRange: ClosedRange<Sentiment>,
    private val origin: Moments
) : Moments {

    override fun count(): Int {
        return toList().size
    }

    override fun find(id: Uuid): Iterable<Moment> {
        return filter { it.id == id }
    }

    override fun mostRecent(): Moment {
        return maxBy { it.timestamp }
    }

    override fun iterator(): Iterator<Moment> {
        val moments = origin.asSequence().filter { it.sentiment in sentimentRange }
        return moments.iterator()
    }
}
