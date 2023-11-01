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
import androidx.test.runner.AndroidJUnit4
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteViewsUpdatingPresenterTest {

    private val remoteViews = mockk<RemoteViews>(relaxed = true)
    private val widgetManager = mockk<AppWidgetManager>(relaxed = true)
    private val widgetId = 0
    private val origin = mockk<Presenter<String>>(relaxed = true)
    private val presenter = RemoteViewsUpdatingPresenter(widgetId, widgetManager, remoteViews, origin)

    @Test
    fun `Updates the remote views after forwarding to origin`() {
        val thing = "Foo"

        presenter.present(thing)

        verify { origin.present(thing) }
        verify { widgetManager.updateAppWidget(widgetId, remoteViews) }
    }
}
