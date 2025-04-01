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

import android.app.Application
import com.benasher44.uuid.Uuid
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.hadisatrio.libs.kotlin.collection.IteratorIterable
import com.hadisatrio.libs.kotlin.geography.Coordinates
import com.hadisatrio.libs.kotlin.geography.LiteralCoordinates
import com.hadisatrio.libs.kotlin.geography.Place
import com.hadisatrio.libs.kotlin.geography.Places
import kotlinx.coroutines.runBlocking
import com.google.android.libraries.places.api.Places as GooglePlaces

class GoogleSdkNearbyPlaces(
    private val coordinates: Coordinates,
    private val limit: Int,
    private val client: PlacesClient
) : Places {

    private val pastResponses: MutableMap<Coordinates, Set<GoogleSdkPlace>> by lazy { mutableMapOf() }
    private val pastSearchResults: MutableSet<GoogleSdkPlace> by lazy { mutableSetOf() }

    constructor(
        coordinates: Coordinates,
        limit: Int,
        apiKey: String,
        application: Application
    ) : this(
        coordinates,
        limit,
        application.run {
            GooglePlaces.initializeWithNewPlacesApiEnabled(application, apiKey)
            GooglePlaces.createClient(application)
        }
    )

    override fun new(): Place {
        throw UnsupportedOperationException("This collection of places is read-only.")
    }

    override fun findPlace(id: Uuid): Iterable<Place> {
        // I don't really like this very much. Ideally, we should be able to perform
        // an ID-based query to Google's services. However, the fact that they are not
        // using UUIDs prevents us from doing so. On the other hand, we have enforced the
        // usage of UUIDs through `Uuid#nameUUIDFromBytes()`, which internally uses
        // MD5, making it nearly impossible to retrieve the original ID.
        // The best we can do then is a simple filter operation on the data we have
        // obtained so far from Google, either through `findPlace(String)` or `iterator()`.
        return pastSearchResults.filter { it.id == id } + this.filter { it.id == id }
    }

    override fun findPlace(name: String): Iterable<Place> {
        val request = SearchByTextRequest.builder(name, GoogleSdkPlaceFields).setMaxResultCount(limit).build()
        val response = Tasks.await(client.searchByText(request))
        val places = response.places.asSequence().map(::GoogleSdkPlace)
        return IteratorIterable { places.onEach { pastSearchResults.add(it) }.iterator() }
    }

    override fun iterator(): Iterator<Place> = runBlocking {
        val coordinates = LiteralCoordinates(coordinates.toString())
        return@runBlocking (cachedPlaces(coordinates) ?: httpPlaces(coordinates)).iterator()
    }

    private fun cachedPlaces(coordinates: Coordinates): Iterable<GoogleSdkPlace>? {
        return pastResponses.entries
            .firstOrNull { (key, _) -> key.distanceTo(coordinates).value <= DISTANCE_THRESHOLD_METERS }
            ?.value
    }

    private fun httpPlaces(coordinates: Coordinates): Iterable<Place> {
        val request = SearchNearbyRequest.builder(bounds(), GoogleSdkPlaceFields).setMaxResultCount(limit).build()
        val response = Tasks.await(client.searchNearby(request))
        val places = response.places.asSequence().map(::GoogleSdkPlace)
        pastResponses[coordinates] = places.toSet()
        return IteratorIterable { places.iterator() }
    }

    private fun bounds(): CircularBounds {
        val coordAsLatLng = coordinates.latlng.let { LatLng(it.first, it.second) }
        return CircularBounds.newInstance(coordAsLatLng, DISTANCE_THRESHOLD_METERS.toDouble())
    }

    companion object {
        private const val DISTANCE_THRESHOLD_METERS = 100
    }
}
