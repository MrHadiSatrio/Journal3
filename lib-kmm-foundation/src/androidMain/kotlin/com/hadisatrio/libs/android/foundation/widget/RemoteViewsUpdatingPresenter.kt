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

import android.appwidget.AppWidgetManager
import android.widget.RemoteViews
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

class RemoteViewsUpdatingPresenter<T>(
    private val widgetId: Int,
    private val widgetManager: AppWidgetManager,
    private val remoteViews: RemoteViews,
    private val origin: Presenter<T>
) : Presenter<T> {

    override fun present(thing: T) {
        origin.present(thing)
        widgetManager.updateAppWidget(widgetId, remoteViews)
    }
}
