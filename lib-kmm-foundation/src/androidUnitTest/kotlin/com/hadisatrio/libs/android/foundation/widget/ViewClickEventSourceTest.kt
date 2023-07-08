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

import android.widget.Button
import androidx.test.runner.AndroidJUnit4
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import io.kotest.matchers.maps.shouldContain
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment

@RunWith(AndroidJUnit4::class)
class ViewClickEventSourceTest {

    @Test
    fun `Produces TextInputEvent on text changes`() = runTest {
        val view = Button(RuntimeEnvironment.getApplication())
        val eventFactory = Event.Factory { SelectionEvent("Foo", "Bar") }
        val events = mutableListOf<Event>()

        val collectJob = launch(UnconfinedTestDispatcher()) {
            ViewClickEventSource(view, eventFactory).events().toList(events)
        }
        view.performClick()

        val description = events.first().describe()
        description.shouldContain("name" to "Selection Event")
        description.shouldContain("selection_kind" to "Foo")
        description.shouldContain("selected_id" to "Bar")
        collectJob.cancel()
    }
}
