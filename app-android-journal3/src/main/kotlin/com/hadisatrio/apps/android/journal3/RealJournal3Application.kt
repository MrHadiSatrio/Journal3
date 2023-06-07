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

import androidx.core.content.ContextCompat
import com.hadisatrio.apps.kotlin.journal3.moment.MemorablesCollection
import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMemorableFiles
import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMemorablePlaces
import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMentionedPeople
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.apps.kotlin.journal3.story.filesystem.FilesystemStories
import com.hadisatrio.libs.android.foundation.activity.CurrentActivity
import com.hadisatrio.libs.android.foundation.modal.AlertDialogModalPresenter
import com.hadisatrio.libs.android.foundation.os.SystemLog
import com.hadisatrio.libs.android.geography.LocationManagerCoordinates
import com.hadisatrio.libs.android.geography.PermissionAwareCoordinates
import com.hadisatrio.libs.android.io.content.ContentResolverSources
import com.hadisatrio.libs.kotlin.foundation.event.EventHub
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSinks
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.presentation.ExecutorDispatchingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import com.hadisatrio.libs.kotlin.geography.Places
import com.hadisatrio.libs.kotlin.geography.here.HereNearbyPlaces
import com.hadisatrio.libs.kotlin.io.SchemeWiseSources
import com.hadisatrio.libs.kotlin.io.filesystem.FileSystemSources
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.datetime.Clock
import okio.FileSystem
import okio.Path.Companion.toPath
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class RealJournal3Application : Journal3Application() {

    override val places: Places by lazy {
        HereNearbyPlaces(
            coordinates = PermissionAwareCoordinates(
                currentActivity = currentActivity,
                origin = LocationManagerCoordinates(this, clock)
            ),
            limit = 100,
            apiKey = BuildConfig.KEY_HERE_API,
            httpClient = HttpClient()
        )
    }

    override val stories: Stories by lazy {
        FilesystemStories(
            fileSystem = FileSystem.SYSTEM,
            path = filesDir.absolutePath.toPath() / "content" / "stories",
            memorables = MemorablesCollection(
                FilesystemMemorablePlaces(
                    fileSystem = FileSystem.SYSTEM,
                    path = filesDir.absolutePath.toPath() / "content" / "places",
                ),
                FilesystemMentionedPeople(
                    fileSystem = FileSystem.SYSTEM,
                    path = filesDir.absolutePath.toPath() / "content" / "people",
                ),
                FilesystemMemorableFiles(
                    fileSystem = FileSystem.SYSTEM,
                    path = filesDir.absolutePath.toPath() / "content" / "attachments",
                    sources = SchemeWiseSources(
                        "file" to FileSystemSources(FileSystem.SYSTEM),
                        "content" to ContentResolverSources(contentResolver)
                    )
                )
            )
        )
    }

    override val modalPresenter: Presenter<Modal> by lazy {
        ExecutorDispatchingPresenter(
            executor = foregroundExecutor,
            origin = AlertDialogModalPresenter(currentActivity, globalEventSource)
        )
    }

    override val currentActivity: CurrentActivity by lazy {
        CurrentActivity(this)
    }

    override val globalEventSink: EventSink by lazy {
        EventSinks(
            ActivityRoutingEventSink(currentActivity),
            SystemLog("Journal3")
        )
    }

    override val globalEventSource: EventHub by lazy {
        EventHub(MutableSharedFlow(extraBufferCapacity = 1))
    }

    override val inactivityAlertThreshold: Duration by lazy {
        3.hours
    }

    override val clock: Clock by lazy {
        Clock.System
    }

    override val backgroundExecutor: Executor by lazy {
        Executors.newCachedThreadPool()
    }

    override val foregroundExecutor: Executor by lazy {
        ContextCompat.getMainExecutor(this)
    }
}
