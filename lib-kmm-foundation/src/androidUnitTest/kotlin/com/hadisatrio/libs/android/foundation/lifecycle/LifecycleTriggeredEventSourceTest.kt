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

package com.hadisatrio.libs.android.foundation.lifecycle

import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.test.runner.AndroidJUnit4
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.SchedulingEventSource
import io.kotest.matchers.maps.shouldContain
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(AndroidJUnit4::class)
class LifecycleTriggeredEventSourceTest {

    @Test
    fun `Produces an event from the given factory when the lifecycle owner enters the specified stage`() {
        val activity = Robolectric.buildActivity(ComponentActivity::class.java)
        val scheduler = TestScheduler()
        val events = mutableListOf<Event>()
        val source = LifecycleTriggeredEventSource(
            lifecycleOwner = activity.get(),
            lifecycleEvent = Lifecycle.Event.ON_RESUME,
            eventFactory = { CompletionEvent() }
        )
        val disposable = SchedulingEventSource(scheduler, source).events().subscribe { events.add(it) }

        activity.setup().resume()

        val description = events.first().describe()
        description.shouldContain("name" to "Completion Event")
        disposable.dispose()
    }
}
