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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.android.journal3.id.getUuidExtra
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.kotlin.journal3.event.RefreshRequestEvent
import com.hadisatrio.apps.kotlin.journal3.story.EditAStoryUseCase
import com.hadisatrio.apps.kotlin.journal3.story.EditableStoryInStories
import com.hadisatrio.apps.kotlin.journal3.story.Story
import com.hadisatrio.apps.kotlin.journal3.story.UpdateDeferringStory
import com.hadisatrio.libs.android.foundation.activity.ActivityCompletionEventSink
import com.hadisatrio.libs.android.foundation.lifecycle.LifecycleTriggeredEventSource
import com.hadisatrio.libs.android.foundation.widget.BackButtonCancellationEventSource
import com.hadisatrio.libs.android.foundation.widget.EditTextInputEventSource
import com.hadisatrio.libs.android.foundation.widget.TextViewStringPresenter
import com.hadisatrio.libs.android.foundation.widget.ViewClickEventSource
import com.hadisatrio.libs.kotlin.foundation.ExecutorDispatchingUseCase
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
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

class EditAStoryActivity : AppCompatActivity() {

    private val presenter: Presenter<Story> by lazy {
        ExecutorDispatchingPresenter(
            executor = journal3Application.foregroundExecutor,
            origin = Presenters(
                AdaptingPresenter(
                    origin = TextViewStringPresenter(findViewById(R.id.title_text_field)),
                    adapter = StoryStringAdapter("title")
                ),
                AdaptingPresenter(
                    origin = TextViewStringPresenter(findViewById(R.id.synopsis_text_field)),
                    adapter = StoryStringAdapter("synopsis")
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
                    view = findViewById(R.id.commit_button),
                    eventFactory = { CompletionEvent() }
                ),
                ViewClickEventSource(
                    view = findViewById(R.id.delete_button),
                    eventFactory = { SelectionEvent("action", "delete") }
                ),
                ViewClickEventSource(
                    view = findViewById(R.id.back_button),
                    eventFactory = { CancellationEvent("user") }
                ),
                EditTextInputEventSource(
                    editText = findViewById(R.id.title_text_field),
                    inputKind = "title"
                ),
                EditTextInputEventSource(
                    editText = findViewById(R.id.synopsis_text_field),
                    inputKind = "synopsis"
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
        ExecutorDispatchingUseCase(
            executor = journal3Application.backgroundExecutor,
            origin = EditAStoryUseCase(
                story = UpdateDeferringStory(
                    origin = EditableStoryInStories(
                        storyId = intent.getUuidExtra("target_id"),
                        stories = journal3Application.stories
                    )
                ),
                stories = journal3Application.stories,
                presenter = presenter,
                modalPresenter = journal3Application.modalPresenter,
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
        setContentView(R.layout.activity_edit_a_story)
        setSupportActionBar(findViewById(R.id.bottom_bar))
    }
}
