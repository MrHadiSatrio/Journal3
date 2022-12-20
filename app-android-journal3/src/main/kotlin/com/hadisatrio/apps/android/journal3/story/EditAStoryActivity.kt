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
import com.hadisatrio.apps.android.journal3.id.BundledTargetId
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.kotlin.journal3.story.EditAStoryUseCase
import com.hadisatrio.libs.android.foundation.activity.ActivityCompletionEventSink
import com.hadisatrio.libs.android.foundation.lifecycle.LifecycleTriggeredEventSource
import com.hadisatrio.libs.android.foundation.widget.BackButtonCancellationEventSource
import com.hadisatrio.libs.android.foundation.widget.EditTextInputEventSource
import com.hadisatrio.libs.android.foundation.widget.TextViewStringPresenter
import com.hadisatrio.libs.android.foundation.widget.ViewClickEventSource
import com.hadisatrio.libs.kotlin.foundation.ExecutorDispatchingUseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSinks
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.ExecutorDispatchingEventSource
import com.hadisatrio.libs.kotlin.foundation.presentation.AdaptingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.ExecutorDispatchingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenters

class EditAStoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_a_story)

        ExecutorDispatchingUseCase(
            executor = journal3Application.backgroundExecutor,
            origin = EditAStoryUseCase(
                targetId = BundledTargetId(intent, "target_id"),
                stories = journal3Application.stories,
                presenter = ExecutorDispatchingPresenter(
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
                ),
                modalPresenter = journal3Application.modalPresenter,
                eventSource = ExecutorDispatchingEventSource(
                    executor = journal3Application.foregroundExecutor,
                    origin = EventSources(
                        journal3Application.globalEventSource,
                        LifecycleTriggeredEventSource(
                            lifecycleOwner = this,
                            lifecycleEvent = Lifecycle.Event.ON_DESTROY,
                            eventFactory = { CancellationEvent("system") }
                        ),
                        ViewClickEventSource(
                            view = findViewById(R.id.add_button),
                            eventFactory = { CompletionEvent() }
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
                ),
                eventSink = EventSinks(
                    journal3Application.globalEventSink,
                    ActivityCompletionEventSink(this)
                )
            )
        )()
    }
}
