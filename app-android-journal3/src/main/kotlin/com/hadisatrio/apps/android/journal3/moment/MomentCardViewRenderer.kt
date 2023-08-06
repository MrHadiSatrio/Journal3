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

package com.hadisatrio.apps.android.journal3.moment

import android.view.View
import android.widget.TextView
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.libs.android.foundation.widget.RecyclerViewPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

object MomentCardViewRenderer : RecyclerViewPresenter.ViewRenderer<Moment> {
    override fun render(view: View, item: Moment) {
        val timestamp = view.journal3Application.timestampDecor.apply(item.timestamp).toString()
        val attachments = "${item.attachments.count()} attachment(s)"
        view.findViewById<TextView>(R.id.timestamp_label).text = timestamp
        view.findViewById<TextView>(R.id.description_label).text = item.description.toString()
        view.findViewById<TextView>(R.id.attachment_count_label).text = attachments
        view.findViewById<TextView>(R.id.place_label).text = item.place.name
        (view.getTag(R.id.presenter_view_tag) as Presenter<Sentiment>).present(item.sentiment)
    }
}
