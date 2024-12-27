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

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Tasks
import com.hadisatrio.libs.kotlin.geography.Coordinates
import com.hadisatrio.libs.kotlin.geography.Speed
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class GmsCoordinates(
    private val client: FusedLocationProviderClient,
    private val clock: Clock
) : Coordinates(), Speed {

    constructor(context: Context, clock: Clock) : this(
        LocationServices.getFusedLocationProviderClient(context),
        clock
    )

    override val latlng: Pair<Double, Double>
        @RequiresPermission(allOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
        get() {
            return location().let { it.latitude to it.longitude }
        }
    override val accuracyInMeters: Float
        @RequiresPermission(allOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
        get() {
            return location().accuracy
        }
    override val value: Double
        @RequiresPermission(allOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
        get() {
            return location().speed.toDouble()
        }

    private var lastDeviceLocation: Location? = null
    private var lastFetchInstant: Instant? = null

    @Synchronized
    @RequiresPermission(allOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    private fun location(): Location {
        val lastFetchInstant = this.lastFetchInstant
        val currentInstant = clock.now()
        val updateRequired =
            lastFetchInstant == null || currentInstant - lastFetchInstant > 10.minutes

        if (updateRequired) {
            val task = client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            this.lastDeviceLocation = Tasks.await(task)
            this.lastFetchInstant = clock.now()
        }

        return this.lastDeviceLocation!!
    }
}
