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

import android.net.Uri
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.core.content.FileProvider
import com.benasher44.uuid.uuid4
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.io.File
import java.util.concurrent.atomic.AtomicReference

class PhotoCaptureEventSource internal constructor(
    private val triggerView: View,
    private val activity: ComponentActivity,
    private val registry: ActivityResultRegistry,
    private val tempDirectory: File
) : EventSource {

    private val uriInFlight = AtomicReference<Uri>(null)
    private val events = MutableSharedFlow<Event>(extraBufferCapacity = 1)

    private val launcher = activity.registerForActivityResult(TakePicture(), registry) { isPictureTaken ->
        val uri = uriInFlight.getAndSet(null)
        if (!isPictureTaken || uri == null) return@registerForActivityResult
        events.tryEmit(SelectionEvent("attachments", uri.toString()))
    }

    init {
        triggerView.setOnClickListener {
            uriInFlight.set(temporaryUri())
            launcher.launch(uriInFlight.get())
        }
    }

    constructor(triggerView: View) : this(
        triggerView,
        triggerView.context as ComponentActivity,
        (triggerView.context as ComponentActivity).activityResultRegistry,
        File(triggerView.context.cacheDir, "camera").apply { mkdirs() }
    )

    override fun events(): Flow<Event> {
        return events
    }

    private fun temporaryUri(): Uri {
        val file = File(tempDirectory, uuid4().toString()).apply { createNewFile() }
        return FileProvider.getUriForFile(activity, activity.application.packageName, file)
    }
}
