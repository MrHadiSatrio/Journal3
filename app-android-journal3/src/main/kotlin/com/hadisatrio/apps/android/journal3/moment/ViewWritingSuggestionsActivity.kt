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

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.android.journal3.sentiment.TextViewColorSentimentPresenter
import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.story.NotabilityFilteringStory
import com.hadisatrio.apps.kotlin.journal3.story.ShowStoryUseCase
import com.hadisatrio.apps.kotlin.journal3.story.Story
import com.hadisatrio.apps.kotlin.journal3.story.cache.CachingStoryPresenter
import com.hadisatrio.libs.android.dimensions.dp
import com.hadisatrio.libs.android.foundation.activity.ActivityCompletionEventSink
import com.hadisatrio.libs.android.foundation.lifecycle.LifecycleTriggeredEventSource
import com.hadisatrio.libs.android.foundation.presentation.ExecutorDispatchingPresenter
import com.hadisatrio.libs.android.foundation.widget.ViewClickEventSource
import com.hadisatrio.libs.android.foundation.widget.recyclerview.ListViewPresenter
import com.hadisatrio.libs.android.foundation.widget.recyclerview.RecyclerViewItemSelectionEventSource
import com.hadisatrio.libs.android.foundation.widget.recyclerview.ViewFactory
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.EventSinks
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.event.SkippingEventSource
import com.hadisatrio.libs.kotlin.foundation.presentation.AdaptingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

class ViewWritingSuggestionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fragment().show(supportFragmentManager, FRAGMENT_TAG)
    }

    class Fragment : BottomSheetDialogFragment() {

        private val presenter: Presenter<Story> by lazy {
            val momentsViewFactory = ViewFactory { parent, _ ->
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
                adapter = { story -> story.moments },
                origin = ListViewPresenter(
                    recyclerView = requireView().findViewById(R.id.moments_list),
                    orientation = RecyclerView.VERTICAL,
                    viewFactory = momentsViewFactory,
                    viewRenderer = MomentCardViewRenderer,
                    differ = MomentItemDiffer,
                    backgroundExecutor = journal3Application.backgroundExecutor
                )
            )

            journal3Application.presenterDecor<Story>().apply(
                CachingStoryPresenter(
                    origin = ExecutorDispatchingPresenter(
                        executor = journal3Application.foregroundExecutor,
                        origin = momentsPresenter
                    )
                )
            )
        }

        private val eventSource: EventSource by lazy {
            journal3Application.eventSourceDecor.apply(
                EventSources(
                    journal3Application.globalEventSource,
                    SkippingEventSource(
                        count = 1,
                        origin = LifecycleTriggeredEventSource(
                            lifecycleOwner = this,
                            lifecycleEvent = Lifecycle.Event.ON_START,
                            eventFactory = { RefreshRequestEvent("lifecycle") }
                        )
                    ),
                    LifecycleTriggeredEventSource(
                        lifecycleOwner = this,
                        lifecycleEvent = Lifecycle.Event.ON_DESTROY,
                        eventFactory = { CancellationEvent("system") }
                    ),
                    ViewClickEventSource(
                        view = requireView().findViewById(R.id.add_button),
                        eventFactory = { SelectionEvent("action", "add_moment") }
                    ),
                    RecyclerViewItemSelectionEventSource(
                        recyclerView = requireView().findViewById(R.id.moments_list)
                    )
                )
            )
        }

        private val eventSink: EventSink by lazy {
            journal3Application.eventSinkDecor.apply(
                EventSinks(
                    journal3Application.globalEventSink,
                    ActivityCompletionEventSink(requireActivity())
                )
            )
        }

        private val useCase: UseCase by lazy {
            journal3Application.useCaseDecor.apply(
                ShowStoryUseCase(
                    story = NotabilityFilteringStory(notable = false, journal3Application.story),
                    presenter = presenter,
                    eventSource = eventSource,
                    eventSink = eventSink
                )
            )
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.fragment_view_writing_suggestions, container, false)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            setupViews()
            useCase()
        }

        private fun setupViews() {
            requireView().findViewById<RecyclerView>(R.id.moments_list).addItemDecoration(
                SpacingItemDecoration(
                    Spacing(
                        edges = Rect(0.dp, 0.dp, 0.dp, 0.dp),
                        horizontal = 0.dp,
                        vertical = 8.dp
                    )
                )
            )
        }
    }

    companion object {
        private val FRAGMENT_TAG = "${ViewWritingSuggestionsActivity::class.simpleName}Fragment"
    }
}
