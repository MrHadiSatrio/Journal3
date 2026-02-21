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

package com.hadisatrio.libs.kotlin.geography.cache

import com.benasher44.uuid.Uuid
import com.hadisatrio.libs.kotlin.geography.Coordinates
import com.hadisatrio.libs.kotlin.geography.Place

/**
 * A [Place] that captures another [Place]'s properties at construction time,
 * isolating callers from subsequent changes to [origin].
 *
 * All [Place] operations not overridden here delegate to [origin].
 *
 * @param id Cached place ID.
 * @param name Cached place name.
 * @param address Cached place address.
 * @param coordinates Cached place coordinates.
 * @param origin The underlying place that other operations delegate to.
 */
class CachingPlace(
    override val id: Uuid,
    override val name: String,
    override val address: String,
    override val coordinates: Coordinates,
    private val origin: Place
) : Place by origin {

    constructor(place: Place) : this(
        place.id,
        place.name,
        place.address,
        place.coordinates,
        place
    )
}
