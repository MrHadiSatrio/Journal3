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

package com.hadisatrio.libs.android.foundation.widget.recyclerview

import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.runner.AndroidJUnit4
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.SchedulingEventSource
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.maps.shouldContain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment

@RunWith(AndroidJUnit4::class)
class RecyclerViewItemSelectionEventSourceTest {

    private val recyclerView = RecyclerView(RuntimeEnvironment.getApplication())
    private val events = mutableListOf<Event>()
    private val scheduler = TestScheduler()
    private val source = SchedulingEventSource(scheduler, RecyclerViewItemSelectionEventSource(recyclerView))

    @Before
    fun `Initialize RecyclerView`() {
        recyclerView.layoutManager = LinearLayoutManager(RuntimeEnvironment.getApplication())
        recyclerView.adapter = Adapter(listOf("Foo", "Bar", "Fizz", "Buzz"))
        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 1000, 1000)
    }

    @Test
    fun `Produces SelectionEvent on clicks on an item within the RecyclerView`() {
        val disposable = SchedulingEventSource(scheduler, source).events().subscribe { events.add(it) }

        recyclerView.dispatchTouchEvent(
            MotionEvent.obtain(
                /* downTime = */
                System.currentTimeMillis(),
                /* eventTime = */
                System.currentTimeMillis(),
                /* action = */
                MotionEvent.ACTION_DOWN,
                /* x = */
                recyclerView.getChildAt(0).x,
                /* y = */
                recyclerView.getChildAt(0).y,
                /* metaState = */
                0
            )
        )
        recyclerView.dispatchTouchEvent(
            MotionEvent.obtain(
                /* downTime = */
                System.currentTimeMillis(),
                /* eventTime = */
                System.currentTimeMillis(),
                /* action = */
                MotionEvent.ACTION_UP,
                /* x = */
                recyclerView.getChildAt(0).x,
                /* y = */
                recyclerView.getChildAt(0).y,
                /* metaState = */
                0
            )
        )

        val description = events.first().describe()
        description.shouldContain("name" to "Selection Event")
        description.shouldContain("selection_kind" to "item_position")
        description.shouldContain("selected_id" to "0")
        disposable.dispose()
    }

    @Test
    fun `Doesn't do anything if touch happens outside of child's boundaries`() {
        val disposable = SchedulingEventSource(scheduler, source).events().subscribe { events.add(it) }

        recyclerView.dispatchTouchEvent(
            MotionEvent.obtain(
                /* downTime = */
                System.currentTimeMillis(),
                /* eventTime = */
                System.currentTimeMillis(),
                /* action = */
                MotionEvent.ACTION_DOWN,
                /* x = */
                recyclerView.getChildAt(0).x,
                /* y = */
                recyclerView.getChildAt(0).y + 800F,
                /* metaState = */
                0
            )
        )
        recyclerView.dispatchTouchEvent(
            MotionEvent.obtain(
                /* downTime = */
                System.currentTimeMillis(),
                /* eventTime = */
                System.currentTimeMillis(),
                /* action = */
                MotionEvent.ACTION_UP,
                /* x = */
                recyclerView.getChildAt(0).x,
                /* y = */
                recyclerView.getChildAt(0).y + 800F,
                /* metaState = */
                0
            )
        )

        disposable.dispose()
    }

    @Test
    fun `Tolerates slight movement whilst registering click events`() {
        val disposable = SchedulingEventSource(scheduler, source).events().subscribe { events.add(it) }

        recyclerView.dispatchTouchEvent(
            MotionEvent.obtain(
                /* downTime = */
                System.currentTimeMillis(),
                /* eventTime = */
                System.currentTimeMillis(),
                /* action = */
                MotionEvent.ACTION_DOWN,
                /* x = */
                recyclerView.getChildAt(0).x,
                /* y = */
                recyclerView.getChildAt(0).y,
                /* metaState = */
                0
            )
        )
        recyclerView.dispatchTouchEvent(
            MotionEvent.obtain(
                /* downTime = */
                System.currentTimeMillis(),
                /* eventTime = */
                System.currentTimeMillis(),
                /* action = */
                MotionEvent.ACTION_MOVE,
                /* x = */
                recyclerView.getChildAt(0).x + 10F,
                /* y = */
                recyclerView.getChildAt(0).y + 10F,
                /* metaState = */
                0
            )
        )
        recyclerView.dispatchTouchEvent(
            MotionEvent.obtain(
                /* downTime = */
                System.currentTimeMillis(),
                /* eventTime = */
                System.currentTimeMillis(),
                /* action = */
                MotionEvent.ACTION_MOVE,
                /* x = */
                recyclerView.getChildAt(0).x + 25F,
                /* y = */
                recyclerView.getChildAt(0).y + 25F,
                /* metaState = */
                0
            )
        )
        recyclerView.dispatchTouchEvent(
            MotionEvent.obtain(
                /* downTime = */
                System.currentTimeMillis(),
                /* eventTime = */
                System.currentTimeMillis(),
                /* action = */
                MotionEvent.ACTION_UP,
                /* x = */
                recyclerView.getChildAt(0).x + 25F,
                /* y = */
                recyclerView.getChildAt(0).y + 25F,
                /* metaState = */
                0
            )
        )

        val description = events.first().describe()
        description.shouldContain("name" to "Selection Event")
        description.shouldContain("selection_kind" to "item_position")
        description.shouldContain("selected_id" to "0")
        disposable.dispose()
    }

    @Test
    fun `Prevents major movements from resulting in a click event`() {
        val disposable = SchedulingEventSource(scheduler, source).events().subscribe { events.add(it) }

        recyclerView.dispatchTouchEvent(
            MotionEvent.obtain(
                /* downTime = */
                System.currentTimeMillis(),
                /* eventTime = */
                System.currentTimeMillis(),
                /* action = */
                MotionEvent.ACTION_DOWN,
                /* x = */
                recyclerView.getChildAt(0).x,
                /* y = */
                recyclerView.getChildAt(0).y,
                /* metaState = */
                0
            )
        )
        recyclerView.dispatchTouchEvent(
            MotionEvent.obtain(
                /* downTime = */
                System.currentTimeMillis(),
                /* eventTime = */
                System.currentTimeMillis(),
                /* action = */
                MotionEvent.ACTION_MOVE,
                /* x = */
                recyclerView.getChildAt(0).x + 100F,
                /* y = */
                recyclerView.getChildAt(0).y + 100F,
                /* metaState = */
                0
            )
        )
        recyclerView.dispatchTouchEvent(
            MotionEvent.obtain(
                /* downTime = */
                System.currentTimeMillis(),
                /* eventTime = */
                System.currentTimeMillis(),
                /* action = */
                MotionEvent.ACTION_MOVE,
                /* x = */
                recyclerView.getChildAt(0).x + 250F,
                /* y = */
                recyclerView.getChildAt(0).y + 250F,
                /* metaState = */
                0
            )
        )
        recyclerView.dispatchTouchEvent(
            MotionEvent.obtain(
                /* downTime = */
                System.currentTimeMillis(),
                /* eventTime = */
                System.currentTimeMillis(),
                /* action = */
                MotionEvent.ACTION_UP,
                /* x = */
                recyclerView.getChildAt(0).x + 250F,
                /* y = */
                recyclerView.getChildAt(0).y + 250F,
                /* metaState = */
                0
            )
        )

        events.shouldBeEmpty()
        disposable.dispose()
    }

    private class Adapter(private val items: List<String>) : RecyclerView.Adapter<ViewHolder>() {

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = TextView(parent.context)
            view.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.view.text = items[position]
        }
    }

    private class ViewHolder constructor(val view: TextView) :
        RecyclerView.ViewHolder(view)
}
