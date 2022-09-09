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

package com.hadisatrio.libs.android.foundation.widget

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RecyclerViewItemSelectionEventSource(
    private val recyclerView: RecyclerView
) : EventSource {

    private val events: Flow<Event> by lazy {
        callbackFlow {
            val listener = object : RecyclerView.SimpleOnItemTouchListener() {

                private var isBeingTouched = false

                override fun onInterceptTouchEvent(view: RecyclerView, event: MotionEvent): Boolean {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        isBeingTouched = true
                    } else if (event.action == MotionEvent.ACTION_UP && isBeingTouched) {
                        isBeingTouched = false
                        val touched = view.findChildViewUnder(event.x, event.y)!!
                        val position = view.getChildAdapterPosition(touched)
                        trySend(SelectionEvent("item_position", position.toString()))
                    } else {
                        isBeingTouched = false
                    }
                    return super.onInterceptTouchEvent(view, event)
                }
            }
            recyclerView.addOnItemTouchListener(listener)
            awaitClose { recyclerView.removeOnItemTouchListener(listener) }
        }
    }

    override fun events(): Flow<Event> {
        return events
    }
}
