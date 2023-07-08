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
import androidx.test.runner.AndroidJUnit4
import com.hadisatrio.libs.kotlin.foundation.event.Event
import io.kotest.matchers.maps.shouldContain
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(AndroidJUnit4::class)
class BackButtonCancellationEventSourceTest {

    @Test
    fun `Produces user-triggered CancellationEvent on Back button presses`() = runTest {
        val activity = Robolectric.setupActivity(ComponentActivity::class.java)
        val events = mutableListOf<Event>()

        val collectJob = launch(UnconfinedTestDispatcher()) {
            BackButtonCancellationEventSource(activity).events().toList(events)
        }
        activity.onBackPressed()

        val description = events.first().describe()
        description.shouldContain("name" to "Cancellation Event")
        description.shouldContain("reason" to "user")
        collectJob.cancel()
    }
}
