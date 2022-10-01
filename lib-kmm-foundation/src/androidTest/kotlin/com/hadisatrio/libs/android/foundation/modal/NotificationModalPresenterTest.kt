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

import android.app.NotificationManager
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.core.app.NotificationCompat
import androidx.test.runner.AndroidJUnit4
import com.hadisatrio.libs.kotlin.foundation.modal.BinaryConfirmationModal
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class NotificationModalPresenterTest {

    @Test
    fun `Presents given modal through a Notification`() {
        val activityController = Robolectric.buildActivity(ComponentActivity::class.java).setup().visible()
        val activity = activityController.get()
        val notificationManager = activity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val shadowNotificationManager = shadowOf(notificationManager)
        val builderFactory = object : NotificationModalPresenter.NotificationBuilderFactory {
            override fun create(): NotificationCompat.Builder {
                return NotificationCompat.Builder(activity, "Foo").apply {
                    setSmallIcon(0)
                    setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    setContentTitle("Fizz")
                    setContentText("Buzz")
                }
            }
        }

        NotificationModalPresenter(activity, { builderFactory }).present(BinaryConfirmationModal("Foo"))

        shadowNotificationManager.allNotifications.size.shouldBe(1)
        shadowNotificationManager.allNotifications.first().channelId.shouldBe("Foo")
    }
}
