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

import android.os.Build
import androidx.test.runner.AndroidJUnit4
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.android.gms.common.api.CommonStatusCodes.ERROR
import com.google.android.gms.common.api.CommonStatusCodes.SUCCESS
import com.hadisatrio.libs.kotlin.geography.ResilientCoordinates
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.BeforeTest
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.Q])
class AndroidCoordinatesTest {

    private val checker = mockk<GoogleApiAvailabilityLight>()
    private val coordinates = AndroidCoordinates(RuntimeEnvironment.getApplication(), TestClock())

    @BeforeTest
    fun `Init mocks`() {
        mockkStatic(GoogleApiAvailabilityLight::class)
        every { GoogleApiAvailabilityLight.getInstance() } returns checker
    }

    @Test
    fun `Delegates to ResilientCoordinates whenever Google Play Services is available`() {
        every { checker.isGooglePlayServicesAvailable(any()) } returns SUCCESS

        coordinates.delegate.shouldBeInstanceOf<ResilientCoordinates>()
    }

    @Test
    fun `Resorts to LocationManagerCoordinates in case Google Play Services is not available`() {
        every { checker.isGooglePlayServicesAvailable(any()) } returns ERROR

        coordinates.delegate.shouldBeInstanceOf<LocationManagerCoordinates>()
    }
}
