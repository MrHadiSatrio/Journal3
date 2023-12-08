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

package com.hadisatrio.apps.android.journal3.moment

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.android.journal3.datetime.TimestampSelectionEventSource
import com.hadisatrio.apps.android.journal3.datetime.TimestampSelectorButtonPresenter
import com.hadisatrio.apps.android.journal3.geography.PlaceSelectionEventSource
import com.hadisatrio.apps.android.journal3.id.getUuidExtra
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
import com.hadisatrio.apps.kotlin.journal3.moment.ClockRespectingMoment
import com.hadisatrio.apps.kotlin.journal3.moment.EditAMomentUseCase
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.moment.SentimentAnalyzingMoment
import com.hadisatrio.apps.kotlin.journal3.moment.UpdateDeferringMoment
import com.hadisatrio.apps.kotlin.journal3.story.EditableMomentInStory
import com.hadisatrio.libs.android.foundation.activity.ActivityCompletionEventSink
import com.hadisatrio.libs.android.foundation.lifecycle.LifecycleTriggeredEventSource
import com.hadisatrio.libs.android.foundation.material.SliderFloatPresenter
import com.hadisatrio.libs.android.foundation.material.SliderSelectionEventSource
import com.hadisatrio.libs.android.foundation.presentation.ExecutorDispatchingPresenter
import com.hadisatrio.libs.android.foundation.widget.BackButtonCancellationEventSource
import com.hadisatrio.libs.android.foundation.widget.EditTextInputEventSource
import com.hadisatrio.libs.android.foundation.widget.PhotoCaptureEventSource
import com.hadisatrio.libs.android.foundation.widget.PhotoSelectionEventSource
import com.hadisatrio.libs.android.foundation.widget.SwitchSelectionEventSource
import com.hadisatrio.libs.android.foundation.widget.TextViewStringPresenter
import com.hadisatrio.libs.android.foundation.widget.ViewClickEventSource
import com.hadisatrio.libs.android.foundation.widget.recyclerview.RecyclerViewPresenter
import com.hadisatrio.libs.android.io.uri.toAndroidUri
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSinks
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.AdaptingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenters

class EditAMomentActivity : AppCompatActivity() {

    private val photoGrid: RecyclerView by lazy {
        findViewById(R.id.photo_grid)
    }

    private val presenter: Presenter<Moment> by lazy {
        ExecutorDispatchingPresenter(
            executor = journal3Application.foregroundExecutor,
            origin = Presenters(
                AdaptingPresenter(
                    origin = TimestampSelectorButtonPresenter(findViewById(R.id.timestamp_selector_button)),
                    adapter = { moment -> moment.timestamp }
                ),
                AdaptingPresenter(
                    origin = TextViewStringPresenter(findViewById(R.id.place_selector_button)),
                    adapter = { moment -> moment.place.name }
                ),
                AdaptingPresenter(
                    origin = TextViewStringPresenter(findViewById(R.id.description_text_field)),
                    adapter = { moment -> moment.description.toString() }
                ),
                AdaptingPresenter(
                    origin = SliderFloatPresenter(findViewById(R.id.sentiment_slider)),
                    adapter = { moment -> moment.sentiment.value }
                ),
                AdaptingPresenter(
                    origin = RecyclerViewPresenter(
                        recyclerView = photoGrid,
                        layoutManager = GridLayoutManager(this, PHOTO_GRID_SPAN_COUNT),
                        viewFactory = { parent, _ ->
                            ImageView(parent.context).apply {
                                layoutParams = RecyclerView.LayoutParams(
                                    photoGrid.measuredHeight,
                                    photoGrid.measuredHeight
                                )
                            }
                        },
                        viewRenderer = { view, item ->
                            Glide.with(view).load(item.toAndroidUri()).centerCrop().into(view as ImageView)
                        }
                    ),
                    adapter = { moment -> moment.attachments }
                )
            )
        )
    }

    private val eventSource: EventSource by lazy {
        journal3Application.eventSourceDecor.apply(
            EventSources(
                journal3Application.globalEventSource,
                LifecycleTriggeredEventSource(
                    lifecycleOwner = this,
                    lifecycleEvent = Lifecycle.Event.ON_RESUME,
                    eventFactory = { RefreshRequestEvent("lifecycle") }
                ),
                LifecycleTriggeredEventSource(
                    lifecycleOwner = this,
                    lifecycleEvent = Lifecycle.Event.ON_DESTROY,
                    eventFactory = { CancellationEvent("system") }
                ),
                ViewClickEventSource(
                    view = findViewById(R.id.back_button),
                    eventFactory = { CancellationEvent("user") }
                ),
                ViewClickEventSource(
                    view = findViewById(R.id.delete_button),
                    eventFactory = { SelectionEvent("action", "delete") }
                ),
                ViewClickEventSource(
                    view = findViewById(R.id.commit_button),
                    eventFactory = { SelectionEvent("action", "commit") }
                ),
                PlaceSelectionEventSource(
                    triggerView = findViewById(R.id.place_selector_button),
                    activity = this
                ),
                TimestampSelectionEventSource(
                    button = findViewById(R.id.timestamp_selector_button)
                ),
                EditTextInputEventSource(
                    editText = findViewById(R.id.description_text_field),
                    inputKind = "description"
                ),
                SliderSelectionEventSource(
                    slider = findViewById(R.id.sentiment_slider),
                    selectionKind = "sentiment"
                ),
                SwitchSelectionEventSource(
                    switch = findViewById(R.id.paraphrase_switch),
                    offEventFactory = { SelectionEvent("action", "disable_paraphrasing") },
                    onEventFactory = { SelectionEvent("action", "enable_paraphrasing") }
                ),
                PhotoCaptureEventSource(
                    triggerView = findViewById(R.id.photo_capturer_button),
                    activity = this
                ),
                PhotoSelectionEventSource(
                    triggerView = findViewById(R.id.photo_picker_button),
                    activity = this
                ),
                BackButtonCancellationEventSource(this)
            )
        )
    }

    private val eventSink: EventSink by lazy {
        EventSinks(
            journal3Application.globalEventSink,
            ActivityCompletionEventSink(this)
        )
    }

    private val useCase: UseCase by lazy {
        journal3Application.useCaseDecor.apply(
            EditAMomentUseCase(
                moment = SentimentAnalyzingMoment(
                    analyst = journal3Application.sentimentAnalyst,
                    origin = ClockRespectingMoment(
                        clock = journal3Application.clock,
                        origin = UpdateDeferringMoment(
                            origin = EditableMomentInStory(
                                storyId = intent.getUuidExtra("story_id"),
                                targetId = intent.getUuidExtra("target_id"),
                                stories = journal3Application.stories
                            )
                        )
                    )
                ),
                stories = journal3Application.stories,
                places = journal3Application.places,
                presenter = presenter,
                modalPresenter = journal3Application.modalPresenter,
                eventSource = eventSource,
                eventSink = eventSink,
                paraphraser = journal3Application.paraphraser
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        useCase()
    }

    private fun setupViews() {
        setContentView(R.layout.activity_edit_a_moment)
        setSupportActionBar(findViewById(R.id.bottom_bar))
    }

    companion object {

        private const val PHOTO_GRID_SPAN_COUNT = 3
    }
}
