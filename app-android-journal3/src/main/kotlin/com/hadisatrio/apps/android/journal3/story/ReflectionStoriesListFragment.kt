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

import android.content.res.Resources
import android.view.LayoutInflater
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.android.journal3.moment.MomentCardViewRenderer
import com.hadisatrio.apps.android.journal3.moment.MomentItemDiffer
import com.hadisatrio.apps.android.journal3.sentiment.TextViewColorSentimentPresenter
import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.apps.kotlin.journal3.story.Story
import com.hadisatrio.apps.kotlin.journal3.story.cache.CachingStoriesPresenter
import com.hadisatrio.libs.android.dimensions.GOLDEN_RATIO
import com.hadisatrio.libs.android.dimensions.dp
import com.hadisatrio.libs.android.foundation.lifecycle.LifecycleTriggeredEventSource
import com.hadisatrio.libs.android.foundation.presentation.ExecutorDispatchingPresenter
import com.hadisatrio.libs.android.foundation.widget.recyclerview.RecyclerViewPresenter
import com.hadisatrio.libs.android.foundation.widget.recyclerview.ViewFactory
import com.hadisatrio.libs.android.foundation.widget.recyclerview.ViewRenderer
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.SchedulingEventSource
import com.hadisatrio.libs.kotlin.foundation.presentation.AdaptingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.PerfTrackingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import kotlin.math.roundToInt

class ReflectionStoriesListFragment : StoriesListFragment() {

    override val stories: Stories by lazy {
        journal3Application.reflections
    }

    override val presenter: Presenter<Stories> by lazy {
        val subItemViewFactory = ViewFactory { parent, _ ->
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.view_moment_vert_card, parent, false)
            val width =
                (Resources.getSystem().displayMetrics.widthPixels / GOLDEN_RATIO).roundToInt()
            val height = (width * GOLDEN_RATIO).roundToInt()
            val sentimentPresenter =
                TextViewColorSentimentPresenter(view.findViewById(R.id.sentiment_indicator))
            view.layoutParams = RecyclerView.LayoutParams(width, height)
            view.setTag(R.id.presenter_view_tag, sentimentPresenter)
            view
        }
        val itemViewFactory = ViewFactory { parent, _ ->
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.view_story_carousel, parent, false)
            val carousel = view.findViewById<RecyclerView>(R.id.moments_carousel)
            val carouselPresenter = RecyclerViewPresenter(
                recyclerView = carousel,
                viewFactory = subItemViewFactory,
                viewRenderer = MomentCardViewRenderer,
                layoutManager = LinearLayoutManager(
                    parent.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                ),
                differ = MomentItemDiffer
            )
            carousel.addItemDecoration(SpacingItemDecoration(Spacing(horizontal = 8.dp)))
            view.setTag(R.id.presenter_view_tag, carouselPresenter)
            view
        }
        val itemViewRenderer = ViewRenderer<Story> { view, item ->
            view.findViewById<TextView>(R.id.title_label).text = item.title
            view.findViewById<TextView>(R.id.synopsis_label).text = item.synopsis.toString()
            (view.getTag(R.id.presenter_view_tag) as Presenter<Iterable<Moment>>).present(item.moments)
        }

        ExecutorDispatchingPresenter(
            executor = journal3Application.backgroundExecutor,
            origin = PerfTrackingPresenter(
                clock = journal3Application.clock,
                eventSink = journal3Application.globalEventSink,
                origin = CachingStoriesPresenter(
                    origin = ExecutorDispatchingPresenter(
                        executor = journal3Application.foregroundExecutor,
                        origin = AdaptingPresenter(
                            adapter = { stories -> stories.toList() },
                            origin = RecyclerViewPresenter(
                                recyclerView = storiesListView,
                                viewFactory = itemViewFactory,
                                viewRenderer = itemViewRenderer,
                                differ = StoryItemDiffer
                            )
                        )
                    )
                )
            )
        )
    }

    override val eventSource: EventSource by lazy {
        SchedulingEventSource(
            subscriptionScheduler = mainScheduler,
            observationScheduler = computationScheduler,
            origin = EventSources(
                journal3Application.globalEventSource,
                LifecycleTriggeredEventSource(
                    lifecycleOwner = this,
                    lifecycleEvent = Lifecycle.Event.ON_START,
                    eventFactory = { RefreshRequestEvent("lifecycle") }
                ),
                LifecycleTriggeredEventSource(
                    lifecycleOwner = this,
                    lifecycleEvent = Lifecycle.Event.ON_DESTROY,
                    eventFactory = { CancellationEvent("system") }
                )
            )
        )
    }
}
