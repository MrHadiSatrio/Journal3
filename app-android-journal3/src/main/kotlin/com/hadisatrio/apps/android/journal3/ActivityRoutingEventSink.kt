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

package com.hadisatrio.apps.android.journal3

import android.content.Intent
import com.hadisatrio.apps.android.journal3.geography.SelectAPlaceActivity
import com.hadisatrio.libs.android.foundation.activity.CurrentActivity
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent

class ActivityRoutingEventSink(
    private val currentActivity: CurrentActivity
) : EventSink {

    override fun sink(event: Event) {
        if (event !is SelectionEvent || event.selectionKind != "action") return
        val identifier = event.selectedIdentifier
        val activity = currentActivity.acquire()
        when (identifier) {
            "select_place" -> activity.startActivity(Intent(activity, SelectAPlaceActivity::class.java))
        }
    }
}
