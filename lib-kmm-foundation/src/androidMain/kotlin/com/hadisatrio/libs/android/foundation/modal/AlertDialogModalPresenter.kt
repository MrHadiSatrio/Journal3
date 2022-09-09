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

import android.app.AlertDialog
import android.content.DialogInterface
import com.hadisatrio.libs.android.foundation.activity.CurrentActivity
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

class AlertDialogModalPresenter(
    private val currentActivity: CurrentActivity,
    private val eventSink: EventSink
) : Presenter<Modal> {

    override fun present(thing: Modal) {
        val builder = AlertDialog.Builder(currentActivity.acquire())
        builder.setTitle(thing.kind)
        builder.setPositiveButton("Positive", OnClickListener(thing.positiveEventFactory, eventSink))
        builder.setNegativeButton("Negative", OnClickListener(thing.negativeEventFactory, eventSink))

        val dialog = builder.create()
        dialog.show()
    }

    private class OnClickListener(
        private val eventFactory: Event.Factory,
        private val eventSink: EventSink
    ) : DialogInterface.OnClickListener {

        override fun onClick(dialog: DialogInterface?, which: Int) {
            eventSink.sink(eventFactory.create())
        }
    }
}
