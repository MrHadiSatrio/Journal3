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
import com.badoo.reaktive.base.setCancellable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import kotlin.math.abs

class RecyclerViewItemSelectionEventSource(
    private val recyclerView: RecyclerView
) : EventSource {

    private val events: Observable<Event> by lazy {
        observable { emitter ->
            val listener = object : RecyclerView.SimpleOnItemTouchListener() {

                private var isBeingPressed = false
                private var initialPressCoordinates = 0F to 0F

                override fun onInterceptTouchEvent(view: RecyclerView, event: MotionEvent): Boolean {
                    when {
                        event.action == MotionEvent.ACTION_DOWN -> {
                            markBeginningOfPress(event.x, event.y)
                        }
                        event.action == MotionEvent.ACTION_MOVE && isBeingPressed -> {
                            if (!isMovementTolerated(event.x, event.y)) markEndOfPress()
                        }
                        event.action == MotionEvent.ACTION_UP && isBeingPressed -> {
                            markEndOfPress()
                            val touched = view.findChildViewUnder(event.x, event.y)
                            if (touched == null) return super.onInterceptTouchEvent(view, event)
                            val position = view.getChildAdapterPosition(touched)
                            emitter.onNext(SelectionEvent("item_position", position.toString()))
                        }
                        else -> markEndOfPress()
                    }
                    return super.onInterceptTouchEvent(view, event)
                }

                private fun markBeginningOfPress(x: Float, y: Float) {
                    isBeingPressed = true
                    initialPressCoordinates = x to y
                }

                private fun isMovementTolerated(x: Float, y: Float): Boolean {
                    val (initialX, initialY) = initialPressCoordinates
                    val xDistance = abs(initialX - x)
                    val yDistance = abs(initialY - y)
                    return xDistance <= MOVEMENT_THRESHOLD_PX && yDistance <= MOVEMENT_THRESHOLD_PX
                }

                private fun markEndOfPress() {
                    isBeingPressed = false
                    initialPressCoordinates = 0F to 0F
                }
            }
            recyclerView.addOnItemTouchListener(listener)
            emitter.setCancellable { recyclerView.removeOnItemTouchListener(listener) }
        }
    }

    override fun events(): Observable<Event> {
        return events
    }

    companion object {
        private const val MOVEMENT_THRESHOLD_PX = 50
    }
}
