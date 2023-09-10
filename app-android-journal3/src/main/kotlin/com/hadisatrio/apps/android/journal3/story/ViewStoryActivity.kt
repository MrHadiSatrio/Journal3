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

package com.hadisatrio.apps.android.journal3.story

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.benasher44.uuid.uuidFrom
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.android.journal3.moment.MomentCardViewRenderer
import com.hadisatrio.apps.android.journal3.moment.MomentItemDiffer
import com.hadisatrio.apps.android.journal3.sentiment.TextViewColorSentimentPresenter
import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.story.ShowStoryUseCase
import com.hadisatrio.apps.kotlin.journal3.story.Story
import com.hadisatrio.apps.kotlin.journal3.story.cache.CachingStoryPresenter
import com.hadisatrio.libs.android.dimensions.dp
import com.hadisatrio.libs.android.foundation.activity.ActivityCompletionEventSink
import com.hadisatrio.libs.android.foundation.lifecycle.LifecycleTriggeredEventSource
import com.hadisatrio.libs.android.foundation.widget.RecyclerViewItemSelectionEventSource
import com.hadisatrio.libs.android.foundation.widget.RecyclerViewPresenter
import com.hadisatrio.libs.android.foundation.widget.TextViewStringPresenter
import com.hadisatrio.libs.android.foundation.widget.ViewClickEventSource
import com.hadisatrio.libs.kotlin.foundation.ExecutorDispatchingUseCase
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSinks
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.ExecutorDispatchingEventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.AdaptingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.ExecutorDispatchingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenters

class ViewStoryActivity : AppCompatActivity() {

    private val presenter: Presenter<Story> by lazy {
        val titlePresenter = AdaptingPresenter(
            adapter = StoryStringAdapter("title"),
            origin = TextViewStringPresenter(findViewById(R.id.title_label))
        )
        val synopsisPresenter = AdaptingPresenter(
            adapter = StoryStringAdapter("synopsis"),
            origin = TextViewStringPresenter(findViewById(R.id.synopsis_label))
        )
        val attachmentPresenter = AdaptingPresenter(
            adapter = StoryStringAdapter("attachment_count"),
            origin = TextViewStringPresenter(findViewById(R.id.attachment_count_label))
        )
        val momentsViewFactory = RecyclerViewPresenter.ViewFactory { parent, _ ->
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.view_moment_horz_card, parent, false)
            val width = RecyclerView.LayoutParams.MATCH_PARENT
            val height = RecyclerView.LayoutParams.WRAP_CONTENT
            val sentimentPresenter = TextViewColorSentimentPresenter(view.findViewById(R.id.sentiment_indicator))
            view.layoutParams = RecyclerView.LayoutParams(width, height)
            view.setTag(R.id.presenter_view_tag, sentimentPresenter)
            view
        }
        val momentsPresenter = AdaptingPresenter<Story, Iterable<Moment>>(
            adapter = { story -> story.moments.toList() },
            origin = RecyclerViewPresenter(
                recyclerView = findViewById(R.id.moments_list),
                viewFactory = momentsViewFactory,
                viewRenderer = MomentCardViewRenderer,
                differ = MomentItemDiffer
            )
        )

        ExecutorDispatchingPresenter(
            executor = journal3Application.backgroundExecutor,
            origin = CachingStoryPresenter(
                origin = ExecutorDispatchingPresenter(
                    executor = journal3Application.foregroundExecutor,
                    origin = Presenters(titlePresenter, synopsisPresenter, attachmentPresenter, momentsPresenter)
                )
            )
        )
    }

    private val eventSource: EventSource by lazy {
        ExecutorDispatchingEventSource(
            executor = journal3Application.foregroundExecutor,
            origin = EventSources(
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
                    view = findViewById(R.id.add_button),
                    eventFactory = { SelectionEvent("action", "add") }
                ),
                ViewClickEventSource(
                    view = findViewById(R.id.edit_button),
                    eventFactory = { SelectionEvent("action", "edit") }
                ),
                ViewClickEventSource(
                    view = findViewById(R.id.delete_button),
                    eventFactory = { SelectionEvent("action", "delete") }
                ),
                ViewClickEventSource(
                    view = findViewById(R.id.back_button),
                    eventFactory = { CancellationEvent("user") }
                ),
                RecyclerViewItemSelectionEventSource(
                    recyclerView = findViewById(R.id.moments_list)
                )
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
        ExecutorDispatchingUseCase(
            executor = journal3Application.backgroundExecutor,
            origin = ShowStoryUseCase(
                storyId = uuidFrom(intent.getStringExtra("target_id")!!),
                stories = journal3Application.stories,
                presenter = presenter,
                eventSource = eventSource,
                eventSink = eventSink
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        useCase()
    }

    private fun setupViews() {
        setContentView(R.layout.activity_view_story)
        setSupportActionBar(findViewById(R.id.bottom_bar))
        findViewById<RecyclerView>(R.id.moments_list).addItemDecoration(
            SpacingItemDecoration(
                Spacing(
                    edges = Rect(0.dp, 16.dp, 0.dp, 16.dp),
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            )
        )
    }
}
