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

package com.hadisatrio.libs.android.foundation.modal

import android.app.AlertDialog
import androidx.activity.ComponentActivity
import androidx.test.runner.AndroidJUnit4
import com.hadisatrio.libs.android.foundation.activity.CurrentActivity
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.modal.BinaryConfirmationModal
import com.hadisatrio.libs.kotlin.foundation.modal.ModalDismissalEvent
import io.kotest.matchers.nulls.shouldNotBeNull
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowAlertDialog

@RunWith(AndroidJUnit4::class)
class AlertDialogModalPresenterTest {

    @After
    fun `Resets shadows`() {
        ShadowAlertDialog.reset()
    }

    @Test
    fun `Presents given modal through an AlertDialog`() {
        val currentActivity = CurrentActivity(RuntimeEnvironment.getApplication())
        val eventSink = mockk<EventSink>(relaxed = true)
        Robolectric.buildActivity(ComponentActivity::class.java).setup().visible()

        AlertDialogModalPresenter(currentActivity, eventSink).present(BinaryConfirmationModal("Foo"))

        ShadowAlertDialog.getLatestDialog().shouldNotBeNull()
    }

    @Test
    @Ignore("Performing clicks doesn't trigger the assigned click listener here; yet it does on a real device")
    fun `Sinks events from the positive Factory on Positive Button clicks`() {
        val currentActivity = CurrentActivity(RuntimeEnvironment.getApplication())
        val eventSink = mockk<EventSink>(relaxed = true)
        Robolectric.buildActivity(ComponentActivity::class.java).setup().visible()

        AlertDialogModalPresenter(currentActivity, eventSink).present(BinaryConfirmationModal("Foo"))
        (ShadowAlertDialog.getLatestDialog() as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).performClick()

        verify(exactly = 1) { eventSink.sink(ModalDismissalEvent("Bar")) }
    }

    @Test
    @Ignore("Performing clicks doesn't trigger the assigned click listener here; yet it does on a real device")
    fun `Sinks events from the negative Factory on Negative Button clicks`() {
        val currentActivity = CurrentActivity(RuntimeEnvironment.getApplication())
        val eventSink = mockk<EventSink>(relaxed = true)
        Robolectric.buildActivity(ComponentActivity::class.java).setup().visible()

        AlertDialogModalPresenter(currentActivity, eventSink).present(BinaryConfirmationModal("Foo"))
        (ShadowAlertDialog.getLatestDialog() as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE).performClick()

        verify(exactly = 1) { eventSink.sink(ModalDismissalEvent("Foo")) }
    }
}
