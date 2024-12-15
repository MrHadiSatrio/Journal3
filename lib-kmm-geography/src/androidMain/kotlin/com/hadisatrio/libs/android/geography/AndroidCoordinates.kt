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

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.Companion.PRIVATE
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.android.gms.common.api.CommonStatusCodes.SUCCESS
import com.hadisatrio.libs.kotlin.geography.Coordinates
import com.hadisatrio.libs.kotlin.geography.Speed
import kotlinx.datetime.Clock

class AndroidCoordinates(
    private val application: Application,
    private val clock: Clock
) : Coordinates(), Speed {

    override val latlng: Pair<Double, Double> get() = delegate.latlng
    override val value: Double get() = (delegate as Speed).value

    @VisibleForTesting(otherwise = PRIVATE)
    internal val delegate: Coordinates by lazy {
        val checker = GoogleApiAvailabilityLight.getInstance()
        val gmsAvailable = checker.isGooglePlayServicesAvailable(application) == SUCCESS
        if (gmsAvailable) {
            GmsCoordinates(application, clock)
        } else {
            LocationManagerCoordinates(application, clock)
        }
    }
}
