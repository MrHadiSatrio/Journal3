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

import android.widget.RemoteViews
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

/**
 * A [Presenter] that sets the text of a [RemoteViews] text view identified by [textViewId].
 *
 * @param remoteViews The [RemoteViews] containing the target text view.
 * @param textViewId Resource ID of the text view within [remoteViews].
 */
class RemoteTextViewStringPresenter(
    private val remoteViews: RemoteViews,
    private val textViewId: Int
) : Presenter<String> {

    override fun present(thing: String) {
        remoteViews.setTextViewText(textViewId, thing)
    }
}
