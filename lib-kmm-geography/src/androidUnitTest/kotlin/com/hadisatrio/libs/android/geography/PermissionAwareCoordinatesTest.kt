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

package com.hadisatrio.libs.android.geography

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.test.runner.AndroidJUnit4
import com.hadisatrio.libs.android.foundation.activity.CurrentActivity
import com.hadisatrio.libs.kotlin.geography.LiteralCoordinates
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows

@RunWith(AndroidJUnit4::class)
class PermissionAwareCoordinatesTest {

    private val activityController = Robolectric.buildActivity(ComponentActivity::class.java)
    private val shadowActivity = Shadows.shadowOf(activityController.get())
    private val currentActivity = CurrentActivity(RuntimeEnvironment.getApplication())
    private val origin = spyk(LiteralCoordinates("-7.607355,110.203804"))
    private val coordinates = PermissionAwareCoordinates(currentActivity, origin)

    @Before
    fun `Setup activity`() {
        activityController.setup().visible()
    }

    @Test
    fun `Forwards the call to origin when permissions are already granted`() {
        shadowActivity.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        coordinates.latitude.shouldBe(origin.latitude)
        coordinates.longitude.shouldBe(origin.longitude)
    }
}
