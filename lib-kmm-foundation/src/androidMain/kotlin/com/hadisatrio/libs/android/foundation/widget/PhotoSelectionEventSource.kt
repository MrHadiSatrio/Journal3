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

import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class PhotoSelectionEventSource internal constructor(
    triggerView: View,
    activity: ComponentActivity,
    registry: ActivityResultRegistry
) : EventSource {

    private val events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    private val launcher = activity.registerForActivityResult(PickMultipleVisualMedia(), registry) { uris ->
        if (uris.isNullOrEmpty()) return@registerForActivityResult
        val csv = uris.joinToString(",")
        events.tryEmit(SelectionEvent("attachments", csv))
    }

    init {
        triggerView.setOnClickListener { launcher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly)) }
    }

    constructor(triggerView: View) : this(
        triggerView,
        triggerView.context as ComponentActivity,
        (triggerView.context as ComponentActivity).activityResultRegistry
    )

    override fun events(): Flow<Event> {
        return events
    }
}
