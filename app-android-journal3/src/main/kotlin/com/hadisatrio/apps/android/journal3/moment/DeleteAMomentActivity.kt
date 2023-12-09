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
import com.benasher44.uuid.uuidFrom
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.kotlin.journal3.moment.DeleteMomentUseCase
import com.hadisatrio.libs.android.foundation.activity.ActivityCompletionEventSink
import com.hadisatrio.libs.android.foundation.presentation.ExecutorDispatchingPresenter
import com.hadisatrio.libs.kotlin.foundation.event.EventSinks
import com.hadisatrio.libs.kotlin.foundation.modal.Modal

class DeleteAMomentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        journal3Application.useCaseDecor.apply(
            DeleteMomentUseCase(
                momentId = uuidFrom(intent.getStringExtra("target_id")!!),
                stories = journal3Application.stories,
                presenter = journal3Application.presenterDecor<Modal>().apply(
                    ExecutorDispatchingPresenter(
                        executor = journal3Application.foregroundExecutor,
                        origin = journal3Application.modalPresenter
                    )
                ),
                eventSource = journal3Application.eventSourceDecor.apply(
                    journal3Application.globalEventSource
                ),
                eventSink = journal3Application.eventSinkDecor.apply(
                    EventSinks(
                        journal3Application.globalEventSink,
                        ActivityCompletionEventSink(this)
                    )
                )
            )
        )
    }
}
