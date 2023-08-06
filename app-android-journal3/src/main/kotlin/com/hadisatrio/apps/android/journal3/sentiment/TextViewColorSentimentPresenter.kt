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

package com.hadisatrio.apps.android.journal3.sentiment

import android.graphics.Color
import android.widget.TextView
import com.hadisatrio.apps.kotlin.journal3.sentiment.NegativeSentimentRange
import com.hadisatrio.apps.kotlin.journal3.sentiment.PositiveSentimentRange
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.sentiment.VeryNegativeSentimentRange
import com.hadisatrio.apps.kotlin.journal3.sentiment.VeryPositiveSentimentRange
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

class TextViewColorSentimentPresenter(
    private val view: TextView
) : Presenter<Sentiment> {

    override fun present(thing: Sentiment) {
        val hexString = when (thing) {
            in VeryPositiveSentimentRange -> "#7CB342"
            in PositiveSentimentRange -> "#C0CA33"
            in NegativeSentimentRange -> "#FB8C00"
            in VeryNegativeSentimentRange -> "#E53935"
            else -> "#FDD835"
        }
        view.setTextColor(Color.parseColor(hexString))
    }
}
