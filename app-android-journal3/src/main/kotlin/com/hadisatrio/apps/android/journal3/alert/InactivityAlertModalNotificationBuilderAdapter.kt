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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.android.journal3.notification.NotificationChannel
import com.hadisatrio.apps.android.journal3.story.ViewStoriesActivity
import com.hadisatrio.libs.android.foundation.modal.NotificationModalPresenter.NotificationBuilderFactory
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.presentation.Adapter

class InactivityAlertModalNotificationBuilderAdapter(
    private val context: Context,
    private val channel: NotificationChannel
) : Adapter<Modal, NotificationBuilderFactory> {

    override fun adapt(thing: Modal): NotificationBuilderFactory {
        require(thing.kind == "inactivity_alert") {
            "Expected modal kind to be 'inactivity_alert'; was ${thing.kind}."
        }

        return object : NotificationBuilderFactory {
            override fun create(): NotificationCompat.Builder {
                return NotificationCompat.Builder(context, channel.id).apply {
                    val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    } else {
                        PendingIntent.FLAG_UPDATE_CURRENT
                    }
                    val pendingIntent = PendingIntent.getActivity(
                        /* context = */
                        context,
                        /* requestCode = */
                        0,
                        /* intent = */
                        Intent(context, ViewStoriesActivity::class.java),
                        /* flags = */
                        pendingIntentFlags
                    )
                    setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    setSmallIcon(R.drawable.ic_notification)
                    setContentTitle(context.getString(R.string.notifTitle_inactivityAlert))
                    setContentText(context.getString(R.string.notifMessage_inactivityAlert))
                    setContentIntent(pendingIntent)
                    setAutoCancel(true)
                }
            }
        }
    }
}
