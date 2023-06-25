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
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.HandlerThread
import androidx.annotation.RequiresPermission
import com.hadisatrio.libs.kotlin.geography.Coordinates
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds

class LocationManagerCoordinates(
    private val manager: LocationManager,
    private val clock: Clock
) : Coordinates() {

    constructor(context: Context, clock: Clock) : this(
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager,
        clock
    )

    override val latitude: Double
        @RequiresPermission(allOf = [ ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION ])
        get() = location().latitude
    override val longitude: Double
        @RequiresPermission(allOf = [ ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION ])
        get() = location().longitude

    private val provider: String? get() {
        val criteria = Criteria().apply { accuracy = Criteria.ACCURACY_FINE }
        return manager.getBestProvider(criteria, true)
    }

    private var lastDeviceLocation: Location? = null
    private var lastFetchInstant: Instant? = null

    @Synchronized
    @RequiresPermission(allOf = [ ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION ])
    private fun location(): Location {
        val lastFetchInstant = this.lastFetchInstant
        val currentInstant = clock.now()
        val updateRequired = lastFetchInstant == null || currentInstant - lastFetchInstant > 10.seconds

        if (updateRequired) {
            val provider = this.provider
            checkNotNull(provider) { "No location providers available." }

            val thread = HandlerThread("LocationManagerCoordinates").also { it.start() }
            val handler = Handler(thread.looper)
            val callback: (location: Location) -> Unit = { location ->
                this@LocationManagerCoordinates.lastDeviceLocation = location
                this@LocationManagerCoordinates.lastFetchInstant = clock.now()
                thread.quit()
            }

            // LocationManager#requestSingleUpdate() is deprecated, but its replacement doesn't play well
            // with the synchronous nature of this class. LocationManager#getCurrentLocation() internally
            // enforces the fetching to happen on a different thread; providing threading control only for
            // the callback.
            handler.post { manager.requestSingleUpdate(provider, callback, null) }
            thread.join()
        }

        return this.lastDeviceLocation!!
    }
}
