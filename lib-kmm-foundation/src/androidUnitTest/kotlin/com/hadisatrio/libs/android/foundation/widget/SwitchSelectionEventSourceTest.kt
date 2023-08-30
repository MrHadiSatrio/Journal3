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

import androidx.activity.ComponentActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.test.runner.AndroidJUnit4
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEvent
import io.kotest.matchers.maps.shouldContain
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(AndroidJUnit4::class)
class SwitchSelectionEventSourceTest {

    private val activityController = Robolectric.buildActivity(ComponentActivity::class.java)
    private val activity = activityController.get()
    private val switch = SwitchCompat(activity)
    private val offEventFactory = Event.Factory { FakeEvent("value" to "off") }
    private val onEventFactory = Event.Factory { FakeEvent("value" to "on") }
    private val eventSource = SwitchSelectionEventSource(switch, offEventFactory, onEventFactory)

    @Test
    fun `Produces Event from the 'off' factory when switch is toggled off`() = runTest {
        val events = mutableListOf<Event>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            eventSource.events().toList(events)
        }

        switch.isChecked = false

        val description = events.last().describe()
        description.shouldContain("value" to "off")
        collectJob.cancel()
    }

    @Test
    fun `Produces Event from the 'on' factory when switch is toggled on`() = runTest {
        val events = mutableListOf<Event>()
        val collectJob = launch(UnconfinedTestDispatcher()) {
            eventSource.events().toList(events)
        }

        switch.isChecked = true

        val description = events.last().describe()
        description.shouldContain("value" to "on")
        collectJob.cancel()
    }
}
