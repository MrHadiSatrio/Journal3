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
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.hadisatrio.apps.android.journal3.JournalScaffoldComposable
import com.hadisatrio.apps.android.journal3.id.getUuidExtra
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.kotlin.journal3.story.EditAStoryUseCase
import com.hadisatrio.apps.kotlin.journal3.story.EditableStoryInStories
import com.hadisatrio.apps.kotlin.journal3.story.UpdateDeferringStory
import com.hadisatrio.libs.android.foundation.ExecutorDispatchingUseCase
import com.hadisatrio.libs.android.foundation.activity.ActivityCompletionEventSink
import com.hadisatrio.libs.android.foundation.lifecycle.LifecycleTriggeredEventSource
import com.hadisatrio.libs.android.foundation.presentation.ExecutorDispatchingPresenter
import com.hadisatrio.libs.android.foundation.widget.BackButtonCancellationEventSource
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSinks
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.SchedulingEventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.AdaptingPresenter

class EditAStoryComposeActivity : AppCompatActivity() {

    private val scaffoldComposable by lazy {
        JournalScaffoldComposable(
            fabEventFactory = { SelectionEvent("action", "commit") },
            content = { storyEditorComposable() }
        )
    }
    private val storyEditorComposable by lazy {
        StoryEditorComposable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { scaffoldComposable() }

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
                presenter = AdaptingPresenter(
                    adapter = { StoryEditorComposable.State(it.title, it.synopsis.toString()) },
                    origin = ExecutorDispatchingPresenter(
                        executor = journal3Application.foregroundExecutor,
                        origin = storyEditorComposable
                    )
                ),
                modalPresenter = journal3Application.modalPresenter,
                eventSource = SchedulingEventSource(
                    mainScheduler,
                    computationScheduler,
                    origin = EventSources(
                        journal3Application.globalEventSource,
                        LifecycleTriggeredEventSource(
                            lifecycleOwner = this,
                            lifecycleEvent = Lifecycle.Event.ON_DESTROY,
                            eventFactory = { CancellationEvent("system") }
                        ),
                        scaffoldComposable,
                        storyEditorComposable,
                        BackButtonCancellationEventSource(this)
                    )
                ),
                eventSink = EventSinks(
                    journal3Application.globalEventSink,
                    ActivityCompletionEventSink(this)
                )
            )
        )()
    }
}
