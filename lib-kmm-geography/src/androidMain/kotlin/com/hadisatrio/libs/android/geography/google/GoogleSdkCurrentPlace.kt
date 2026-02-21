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

package com.hadisatrio.libs.android.geography.google

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Application
import androidx.annotation.RequiresPermission
import com.benasher44.uuid.Uuid
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.hadisatrio.libs.kotlin.geography.Coordinates
import com.hadisatrio.libs.kotlin.geography.Place
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds

/**
 * A [Place] that always reflects the most likely current location as determined by the Google
 * Places SDK.
 *
 * Refreshes the underlying place at most once every 10 seconds. Requires ACCESS_FINE_LOCATION
 * and ACCESS_COARSE_LOCATION permissions.
 *
 * @param client Places client used to query the current place.
 * @param clock Clock used to throttle place refresh intervals.
 */
@SuppressLint(
    "MissingPermission"
    /* We are expecting the client to handle permissions as expressed on the annotated
     * properties seen in this class. However, as the delegate (from which this requirement
     * is coming from) is being instantiated lazily, we could not specify the annotation
     * on the delegate itself. Hence the suppression. */
)
class GoogleSdkCurrentPlace(
    private val client: PlacesClient,
    private val clock: Clock
) : Place {

    override val id: Uuid
        @RequiresPermission(allOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
        get() = delegate().id

    override val name: String
        @RequiresPermission(allOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
        get() = delegate().name

    override val address: String
        @RequiresPermission(allOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
        get() = delegate().address

    override val coordinates: Coordinates
        @RequiresPermission(allOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
        get() = delegate().coordinates

    private var lastKnownPlace: Place? = null
    private var lastFetchInstant: Instant? = null

    @Synchronized
    @RequiresPermission(allOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    private fun delegate(): Place {
        val lastFetchInstant = this.lastFetchInstant
        val currentInstant = clock.now()
        val updateRequired = lastFetchInstant == null || currentInstant - lastFetchInstant > 10.seconds

        if (updateRequired) {
            val request = FindCurrentPlaceRequest.builder(GoogleSdkPlaceFields).build()
            val response = Tasks.await(client.findCurrentPlace(request))
            val googlePlace = response.placeLikelihoods.maxBy { it.likelihood }.place
            this.lastKnownPlace = GoogleSdkPlace(googlePlace)
            this.lastFetchInstant = clock.now()
        }

        return this.lastKnownPlace!!
    }

    constructor(
        apiKey: String,
        application: Application,
        clock: Clock
    ) : this(
        application.run {
            Places.initializeWithNewPlacesApiEnabled(application, apiKey)
            Places.createClient(application)
        },
        clock
    )
}
