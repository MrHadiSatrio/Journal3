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
import com.google.android.material.color.DynamicColors
import com.hadisatrio.apps.android.journal3.sentiment.TfliteSentimentAnalyst
import com.hadisatrio.apps.kotlin.journal3.datetime.FormattedTimestamp
import com.hadisatrio.apps.kotlin.journal3.datetime.LiteralTimestamp
import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.moment.CountLimitingMoments
import com.hadisatrio.apps.kotlin.journal3.moment.MergedMemorables
import com.hadisatrio.apps.kotlin.journal3.moment.OrderRandomizingMoments
import com.hadisatrio.apps.kotlin.journal3.moment.SentimentRangedMoments
import com.hadisatrio.apps.kotlin.journal3.moment.TimeRangedMoments
import com.hadisatrio.apps.kotlin.journal3.moment.VicinityMoments
import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMemorableFiles
import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMemorablePlaces
import com.hadisatrio.apps.kotlin.journal3.moment.filesystem.FilesystemMentionedPeople
import com.hadisatrio.apps.kotlin.journal3.sentiment.NegativeishSentimentRange
import com.hadisatrio.apps.kotlin.journal3.sentiment.PositiveishSentimentRange
import com.hadisatrio.apps.kotlin.journal3.sentiment.SentimentAnalyst
import com.hadisatrio.apps.kotlin.journal3.sentiment.VeryPositiveSentimentRange
import com.hadisatrio.apps.kotlin.journal3.story.InitDeferringStories
import com.hadisatrio.apps.kotlin.journal3.story.MomentfulStories
import com.hadisatrio.apps.kotlin.journal3.story.Reflection
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.apps.kotlin.journal3.story.filesystem.FilesystemStories
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.android.foundation.activity.CurrentActivity
import com.hadisatrio.libs.android.foundation.modal.AlertDialogModalPresenter
import com.hadisatrio.libs.android.foundation.os.SystemLog
import com.hadisatrio.libs.android.geography.LocationManagerCoordinates
import com.hadisatrio.libs.android.geography.PermissionAwareCoordinates
import com.hadisatrio.libs.android.io.content.ContentResolverSources
import com.hadisatrio.libs.kotlin.foundation.event.EventHub
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSinks
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
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
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class RealJournal3Application : Journal3Application() {

    override val places: Places by lazy {
        HereNearbyPlaces(
            coordinates = coordinates,
            limit = 100,
            apiKey = BuildConfig.KEY_HERE_API,
            httpClient = HttpClient()
        )
    }

    override val stories: Stories by lazy {
        FilesystemStories(
            fileSystem = FileSystem.SYSTEM,
            path = filesDir.absolutePath.toPath() / "content" / "stories",
            memorables = MergedMemorables(
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

    override val reflections: Stories by lazy {
        InitDeferringStories {
            MomentfulStories(
                FakeStories(
                    Reflection(
                        title = "Around the Corner",
                        synopsis = TokenableString("Rediscover moments from nearby places."),
                        moments = CountLimitingMoments(
                            limit = 10,
                            origin = OrderRandomizingMoments(
                                origin = VicinityMoments(
                                    coordinates = coordinates,
                                    distanceLimitInM = 100.0,
                                    origin = stories.moments
                                )
                            )
                        )
                    ),
                    Reflection(
                        title = "Recent Wins",
                        synopsis = TokenableString("Celebrate positive moments of the week."),
                        moments = CountLimitingMoments(
                            limit = 10,
                            origin = OrderRandomizingMoments(
                                origin = TimeRangedMoments(
                                    timeRange = LiteralTimestamp(clock.now() - 7.days)..LiteralTimestamp(clock.now()),
                                    origin = SentimentRangedMoments(
                                        sentimentRange = PositiveishSentimentRange,
                                        origin = stories.moments
                                    )
                                )
                            )
                        )
                    ),
                    Reflection(
                        title = "Positivity Overload",
                        synopsis = TokenableString("Immerse in incredibly positive moments."),
                        moments = CountLimitingMoments(
                            limit = 10,
                            origin = OrderRandomizingMoments(
                                origin = SentimentRangedMoments(
                                    sentimentRange = VeryPositiveSentimentRange,
                                    origin = stories.moments
                                )
                            )
                        )
                    ),
                    Reflection(
                        title = "Shadows & Light",
                        synopsis = TokenableString("Reflect on deeply emotional moments."),
                        moments = CountLimitingMoments(
                            limit = 5,
                            origin = OrderRandomizingMoments(
                                origin = SentimentRangedMoments(
                                    sentimentRange = NegativeishSentimentRange,
                                    origin = stories.moments
                                )
                            )
                        )
                    )
                )
            )
        }
    }

    override val modalPresenter: Presenter<Modal> by lazy {
        ExecutorDispatchingPresenter(
            executor = foregroundExecutor,
            origin = AlertDialogModalPresenter(currentActivity, globalEventSource as EventHub)
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

    override val globalEventSource: EventSource by lazy {
        EventHub(MutableSharedFlow(extraBufferCapacity = 1))
    }

    override val timestampDecor: Timestamp.Decor by lazy {
        Timestamp.Decor { FormattedTimestamp("EEEE, d MMMM yyyy '·' hh:mm aa", it) }
    }

    override val inactivityAlertThreshold: Duration by lazy {
        3.hours
    }

    override val sentimentAnalyst: SentimentAnalyst by lazy {
        val modelFile = File(cacheDir, "text_classification.tflite")
        if (!modelFile.exists()) {
            val inStream = assets.open("text_classification.tflite")
            val outStream = modelFile.outputStream()
            inStream.use { outStream.use { inStream.copyTo(outStream) } }
        }
        TfliteSentimentAnalyst(modelFile)
    }

    override val clock: Clock by lazy {
        Clock.System
    }

    private val coordinates by lazy {
        PermissionAwareCoordinates(
            currentActivity = currentActivity,
            origin = LocationManagerCoordinates(this, clock)
        )
    }

    override val backgroundExecutor: Executor by lazy {
        Executors.newCachedThreadPool()
    }

    override val foregroundExecutor: Executor by lazy {
        ContextCompat.getMainExecutor(this)
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
