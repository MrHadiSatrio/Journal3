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

package com.hadisatrio.apps.android.journal3.geography

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.subject.publish.PublishSubject
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.RxEventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent

class PlaceSelectionEventSource internal constructor(
    triggerView: View,
    activity: ComponentActivity,
    registry: ActivityResultRegistry
) : RxEventSource {

    private val events = PublishSubject<Event>()
    private val launcher = activity.registerForActivityResult(SelectAPlace(), registry) { placeId ->
        if (placeId.isNullOrBlank()) return@registerForActivityResult
        events.onNext(SelectionEvent("place", placeId))
    }

    init {
        triggerView.setOnClickListener { launcher.launch(Unit) }
    }

    constructor(triggerView: View, activity: ComponentActivity) : this(
        triggerView,
        activity,
        activity.activityResultRegistry
    )

    override fun events(): Observable<Event> {
        return events
    }

    private class SelectAPlace : ActivityResultContract<Unit, String>() {

        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(context, SelectAPlaceActivity::class.java)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): String {
            return intent?.getStringExtra("place") ?: ""
        }
    }
}
