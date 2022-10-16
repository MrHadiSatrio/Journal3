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

package com.hadisatrio.apps.android.journal3.notification

import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import com.hadisatrio.apps.android.journal3.R

enum class NotificationChannel(
    val id: String,
    @StringRes
    private val nameResId: Int,
    @StringRes
    private val descriptionResId: Int,
    @StringRes
    private val importance: Int
) {

    ALERT_AND_REMINDERS(
        id = "68683bb1-d1e0-488e-8569-c711b3167617",
        nameResId = R.string.notifChannelName_alertsReminders,
        descriptionResId = R.string.notifChannelDesc_alertsReminders,
        importance = NotificationManagerCompat.IMPORTANCE_DEFAULT
    );

    fun create(context: Context) {
        create(context.resources, context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
    }

    fun create(resources: Resources, notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = resources.getString(nameResId)
            val description = resources.getString(descriptionResId)
            val channel = android.app.NotificationChannel(id, name, importance)
            channel.description = description
            notificationManager.createNotificationChannel(channel)
        }
    }
}
