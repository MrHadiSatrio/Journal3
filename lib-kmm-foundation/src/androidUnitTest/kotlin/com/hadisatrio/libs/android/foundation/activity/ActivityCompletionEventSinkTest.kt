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

package com.hadisatrio.libs.android.foundation.activity

import androidx.activity.ComponentActivity
import androidx.test.runner.AndroidJUnit4
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.event.TextInputEvent
import com.hadisatrio.libs.kotlin.foundation.modal.ModalApprovalEvent
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(AndroidJUnit4::class)
class ActivityCompletionEventSinkTest {

    private val activityController = Robolectric.buildActivity(ComponentActivity::class.java)
    private val activity = spyk(activityController.get())
    private val eventSink = ActivityCompletionEventSink(activity)

    @Before
    fun `Starts activity`() {
        activityController.setup().visible()
    }

    @Test
    fun `Finishes the activity upon receiving a completion event`() {
        eventSink.sink(CompletionEvent())
        verify(exactly = 1) { activity.finish() }
    }

    @Test
    fun `Does not try to finish if the activity is already finishing`() {
        activity.finish()
        eventSink.sink(CompletionEvent())
        verify(exactly = 1) { activity.finish() }
    }

    @Test
    fun `Does not try to finish if the activity is changing configurations`() {
        every { activity.isChangingConfigurations }.returns(true)
        eventSink.sink(CompletionEvent())
        verify(inverse = true) { activity.finish() }
    }

    @Test
    fun `Does nothing to the activity upon receiving other events`() {
        arrayOf(
            TextInputEvent("foo", "Bar"),
            SelectionEvent("fizz", "buzz"),
            ModalApprovalEvent("lorem"),
            CancellationEvent("system"),
        ).forEach { event -> eventSink.sink(event) }
        verify(inverse = true) { activity.finish() }
    }
}
