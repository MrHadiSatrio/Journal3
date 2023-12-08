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
import com.badoo.reaktive.subject.publish.PublishSubject
import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.sentiment.DumbSentimentAnalyst
import com.hadisatrio.apps.kotlin.journal3.sentiment.SentimentAnalyst
import com.hadisatrio.apps.kotlin.journal3.story.SelfPopulatingStories
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.libs.android.foundation.activity.CurrentActivity
import com.hadisatrio.libs.kotlin.foundation.Decor
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.EventHub
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEventSink
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import com.hadisatrio.libs.kotlin.foundation.presentation.fake.FakePresenter
import com.hadisatrio.libs.kotlin.geography.Places
import com.hadisatrio.libs.kotlin.geography.SelfPopulatingPlaces
import com.hadisatrio.libs.kotlin.geography.fake.FakePlaces
import com.hadisatrio.libs.kotlin.paraphrase.DumbParaphraser
import com.hadisatrio.libs.kotlin.paraphrase.Paraphraser
import kotlinx.datetime.Clock
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class FakeJournal3Application : Journal3Application() {

    override val places: Places by lazy { SelfPopulatingPlaces(10, FakePlaces()) }
    override val stories: Stories by lazy { SelfPopulatingStories(10, 10, FakeStories()) }
    override val reflections: Stories by lazy { FakeStories() }
    override val modalPresenter: Presenter<Modal> by lazy { FakePresenter() }
    override val currentActivity: CurrentActivity by lazy { CurrentActivity(this) }
    override val globalEventSink: EventSink by lazy { FakeEventSink() }
    override val globalEventSource: EventSource by lazy { EventHub(PublishSubject()) }
    override val timestampDecor: Decor<Timestamp> by lazy { Decor<Timestamp> { it } }
    override val useCaseDecor: Decor<UseCase> by lazy { Decor<UseCase> { it } }
    override val eventSourceDecor: Decor<EventSource> by lazy { Decor<EventSource> { it } }
    override val inactivityAlertThreshold: Duration by lazy { 3.hours }
    override val paraphraser: Paraphraser by lazy { DumbParaphraser }
    override val sentimentAnalyst: SentimentAnalyst by lazy { DumbSentimentAnalyst }
    override val clock: Clock by lazy { Clock.System }
    override val backgroundExecutor: Executor by lazy { Executors.newFixedThreadPool(2) }
    override val foregroundExecutor: Executor by lazy { ContextCompat.getMainExecutor(this) }
}
