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
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.runner.AndroidJUnit4
import com.hadisatrio.libs.kotlin.foundation.event.Event
import io.kotest.matchers.maps.shouldContain
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment

@RunWith(AndroidJUnit4::class)
class RecyclerViewItemSelectionEventSourceTest {

    @Test
    fun `Produces SelectionEvent on clicks on an item within the RecyclerView`() = runTest {
        val recyclerView = RecyclerView(RuntimeEnvironment.getApplication())
        val events = mutableListOf<Event>()
        recyclerView.layoutManager = LinearLayoutManager(RuntimeEnvironment.getApplication())
        recyclerView.adapter = Adapter(listOf("Foo", "Bar", "Fizz", "Buzz"))
        recyclerView.measure(0, 0)
        recyclerView.layout(0, 0, 1000, 1000)

        val collectJob = launch(UnconfinedTestDispatcher()) {
            RecyclerViewItemSelectionEventSource(recyclerView).events().toList(events)
        }
        recyclerView.dispatchTouchEvent(
            MotionEvent.obtain(
                /* downTime = */ System.currentTimeMillis(),
                /* eventTime = */ System.currentTimeMillis(),
                /* action = */ MotionEvent.ACTION_DOWN,
                /* x = */ recyclerView.getChildAt(0).x,
                /* y = */ recyclerView.getChildAt(0).y,
                /* metaState = */ 0
            )
        )
        recyclerView.dispatchTouchEvent(
            MotionEvent.obtain(
                /* downTime = */ System.currentTimeMillis(),
                /* eventTime = */ System.currentTimeMillis(),
                /* action = */ MotionEvent.ACTION_UP,
                /* x = */ recyclerView.getChildAt(0).x,
                /* y = */ recyclerView.getChildAt(0).y,
                /* metaState = */ 0
            )
        )

        val description = events.first().describe()
        description.shouldContain("name" to "Selection Event")
        description.shouldContain("selection_kind" to "item_position")
        description.shouldContain("selected_id" to "0")
        collectJob.cancel()
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
