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

import android.app.Application
import android.content.Context
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.libs.android.foundation.activity.CurrentActivity
import com.hadisatrio.libs.kotlin.foundation.event.EventHub
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import com.hadisatrio.libs.kotlin.geography.Places
import kotlinx.datetime.Clock
import java.util.concurrent.Executor
import kotlin.time.Duration

abstract class Journal3Application : Application() {
    abstract val places: Places
    abstract val stories: Stories
    abstract val modalPresenter: Presenter<Modal>
    abstract val currentActivity: CurrentActivity
    abstract val globalEventSink: EventSink
    abstract val globalEventSource: EventHub
    abstract val inactivityAlertThreshold: Duration
    abstract val clock: Clock
    abstract val backgroundExecutor: Executor
    abstract val foregroundExecutor: Executor
}

val Context.journal3Application: Journal3Application
    get() = applicationContext as Journal3Application
