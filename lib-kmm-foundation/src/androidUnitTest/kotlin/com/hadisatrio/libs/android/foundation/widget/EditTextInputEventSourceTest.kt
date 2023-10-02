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

import android.widget.EditText
import androidx.test.runner.AndroidJUnit4
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.SchedulingEventSource
import io.kotest.matchers.maps.shouldContain
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment

@RunWith(AndroidJUnit4::class)
class EditTextInputEventSourceTest {

    @Test
    fun `Produces TextInputEvent on text changes`() {
        val editText = EditText(RuntimeEnvironment.getApplication())
        val inputKind = "Foo"
        val scheduler = TestScheduler()
        val events = mutableListOf<Event>()
        val source = EditTextInputEventSource(editText, inputKind)
        val disposable = SchedulingEventSource(scheduler, source).events().subscribe { events.add(it) }

        editText.setText("Bar")

        val description = events.first().describe()
        description.shouldContain("name" to "Text Input Event")
        description.shouldContain("input_kind" to inputKind)
        description.shouldContain("input_value" to "Bar")
        disposable.dispose()
    }
}
