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

package com.hadisatrio.libs.android.foundation.widget

import android.widget.TextView
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

/**
 * A [Presenter] that sets the text of a [TextView].
 *
 * The text is only updated if it differs from the current value, avoiding redundant redraws.
 *
 * @param textView The [TextView] to update.
 */
class TextViewStringPresenter(
    private val textView: TextView
) : Presenter<String> {

    override fun present(thing: String) {
        if (textView.text.toString() == thing) return
        textView.text = thing
    }
}
