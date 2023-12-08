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

package com.hadisatrio.apps.android.journal3.alert

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.android.journal3.notification.NotificationChannel
import com.hadisatrio.apps.kotlin.journal3.alert.AlertInactivityUseCase
import com.hadisatrio.libs.android.foundation.modal.NotificationModalPresenter
import com.hadisatrio.libs.kotlin.foundation.event.NoOpEventSource

@Suppress("unused")
class InactivityAlertingWork(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val application = context.journal3Application
        AlertInactivityUseCase(
            threshold = application.inactivityAlertThreshold,
            stories = application.stories,
            presenter = NotificationModalPresenter(
                context = context,
                contentAdapter = InactivityAlertModalNotificationBuilderAdapter(
                    context = context,
                    channel = NotificationChannel.ALERT_AND_REMINDERS
                ),
            ),
            eventSource = application.eventSourceDecor.apply(NoOpEventSource),
            eventSink = application.globalEventSink
        )()
        return Result.success()
    }
}
