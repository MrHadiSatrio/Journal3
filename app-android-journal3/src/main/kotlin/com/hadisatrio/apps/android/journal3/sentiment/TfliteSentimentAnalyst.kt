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

import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.sentiment.SentimentAnalyst
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier
import org.tensorflow.lite.task.text.nlclassifier.NLClassifier.NLClassifierOptions
import java.io.File

class TfliteSentimentAnalyst(
    private val model: File
) : SentimentAnalyst {

    private val classifier: NLClassifier by lazy {
        NLClassifier.createFromFileAndOptions(
            model,
            NLClassifierOptions.builder()
                .setBaseOptions(BaseOptions.builder().useNnapi().build())
                .build()
        )
    }

    override fun train(relationships: Map<String, Sentiment>) {
        // Do nothing.
    }

    override fun analyze(text: String): Sentiment {
        val result: List<Category> = classifier.classify(text)
        val positiveLabel: Category = result[1]
        return Sentiment(positiveLabel.score)
    }
}
