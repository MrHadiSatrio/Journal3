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

package com.hadisatrio.libs.android.foundation.modal

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.presentation.Adapter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

/**
 * A [Presenter] that posts a system notification for the given [Modal].
 *
 * Requires the `POST_NOTIFICATIONS` permission. The notification ID is derived from the
 * hash code of [Modal.kind].
 *
 * @param context Context used to obtain the [NotificationManagerCompat].
 * @param contentAdapter Converts a [Modal] into a [NotificationBuilderFactory].
 */
class NotificationModalPresenter(
    private val context: Context,
    private val contentAdapter: Adapter<Modal, NotificationBuilderFactory>
) : Presenter<Modal> {

    @RequiresPermission(POST_NOTIFICATIONS)
    override fun present(thing: Modal) {
        val builder = contentAdapter.adapt(thing).create()
        val manager = NotificationManagerCompat.from(context)
        manager.notify(thing.kind.hashCode(), builder.build())
    }

    /**
     * A factory for [NotificationCompat.Builder]s configured for a specific [Modal].
     */
    fun interface NotificationBuilderFactory {
        /**
         * Returns a fully configured [NotificationCompat.Builder].
         */
        fun create(): NotificationCompat.Builder
    }
}
