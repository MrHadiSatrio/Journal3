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

import android.app.Application
import android.content.Context
import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMomentfulPlaces
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.apps.kotlin.journal3.story.filesystem.FilesystemStories
import com.hadisatrio.libs.android.foundation.activity.CurrentActivity
import com.hadisatrio.libs.android.foundation.modal.AlertDialogModalPresenter
import com.hadisatrio.libs.android.foundation.os.SystemLog
import com.hadisatrio.libs.kotlin.foundation.event.EventHub
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.presentation.CoroutineDispatchingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.datetime.Clock
import okio.FileSystem
import okio.Path.Companion.toPath
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class Journal3 : Application() {

    val stories: Stories by lazy {
        FilesystemStories(
            fileSystem = FileSystem.SYSTEM,
            path = filesDir.absolutePath.toPath() / "content" / "stories",
            places = FilesystemMomentfulPlaces(
                fileSystem = FileSystem.SYSTEM,
                path = filesDir.absolutePath.toPath() / "content" / "places",
            )
        )
    }

    val modalPresenter: Presenter<Modal> by lazy {
        CoroutineDispatchingPresenter(
            coroutineScope = globalCoroutineScope,
            coroutineDispatcher = Dispatchers.Main,
            origin = AlertDialogModalPresenter(currentActivity, globalEventSource)
        )
    }

    val currentActivity: CurrentActivity by lazy {
        CurrentActivity(this)
    }

    val globalEventSink: EventSink by lazy {
        SystemLog("Journal3")
    }

    val globalCoroutineScope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Default)
    }

    val globalEventSource: EventHub by lazy {
        EventHub(MutableSharedFlow(extraBufferCapacity = 1))
    }

    val inactivityAlertThreshold: Duration by lazy {
        3.hours
    }

    val clock: Clock by lazy {
        Clock.System
    }

    companion object {

        val Context.journal3Application: Journal3
            get() = applicationContext as Journal3
    }
}
