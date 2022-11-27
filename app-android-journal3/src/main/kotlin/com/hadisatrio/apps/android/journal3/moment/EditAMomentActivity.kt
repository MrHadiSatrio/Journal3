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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hadisatrio.apps.android.journal3.ActivityRouter
import com.hadisatrio.apps.android.journal3.Journal3.Companion.journal3Application
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.android.journal3.datetime.TimestampSelectionEventSource
import com.hadisatrio.apps.android.journal3.id.BundledTargetId
import com.hadisatrio.apps.kotlin.journal3.moment.EditAMomentUseCase
import com.hadisatrio.libs.android.foundation.material.SliderFloatPresenter
import com.hadisatrio.libs.android.foundation.material.SliderSelectionEventSource
import com.hadisatrio.libs.android.foundation.widget.BackButtonCancellationEventSource
import com.hadisatrio.libs.android.foundation.widget.CoroutineDispatchingEventSource
import com.hadisatrio.libs.android.foundation.widget.EditTextInputEventSource
import com.hadisatrio.libs.android.foundation.widget.TextViewStringPresenter
import com.hadisatrio.libs.android.foundation.widget.ViewClickEventSource
import com.hadisatrio.libs.kotlin.foundation.CoroutineDispatchingUseCase
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.AdaptingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.CoroutineDispatchingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenters
import kotlinx.coroutines.Dispatchers

class EditAMomentActivity : AppCompatActivity() {

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_a_moment)

        CoroutineDispatchingUseCase(
            coroutineScope = lifecycleScope,
            coroutineDispatcher = Dispatchers.Default,
            origin = EditAMomentUseCase(
                targetId = BundledTargetId(intent, "target_id"),
                storyId = BundledTargetId(intent, "story_id"),
                stories = journal3Application.stories,
                places = journal3Application.places,
                presenter = CoroutineDispatchingPresenter(
                    coroutineScope = lifecycleScope,
                    coroutineDispatcher = Dispatchers.Main,
                    origin = Presenters(
                        AdaptingPresenter(
                            origin = TextViewStringPresenter(findViewById(R.id.timestamp_selector_button)),
                            adapter = { moment -> moment.timestamp.toString() }
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
                        )
                    )
                ),
                modalPresenter = journal3Application.modalPresenter,
                eventSource = CoroutineDispatchingEventSource(
                    coroutineDispatcher = Dispatchers.Main,
                    origin = EventSources(
                        journal3Application.globalEventSource,
                        ViewClickEventSource(
                            view = findViewById(R.id.add_button),
                            eventFactory = { CompletionEvent() }
                        ),
                        ViewClickEventSource(
                            view = findViewById(R.id.place_selector_button),
                            eventFactory = { SelectionEvent("action", "select_place") }
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
                        BackButtonCancellationEventSource(this)
                    )
                ),
                eventSink = journal3Application.globalEventSink,
                router = ActivityRouter(this),
                clock = journal3Application.clock
            )
        )()
    }
}
