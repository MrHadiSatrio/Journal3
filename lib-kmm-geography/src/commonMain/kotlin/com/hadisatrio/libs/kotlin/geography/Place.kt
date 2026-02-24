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

package com.hadisatrio.libs.kotlin.geography

import com.benasher44.uuid.Uuid

/**
 * A named, addressable geographic location with a stable [id] and [coordinates].
 */
interface Place {
    val id: Uuid
    val name: String
    val address: String
    val coordinates: Coordinates

    /**
     * Returns the [Distance] between this place and [other].
     *
     * @param other The other place.
     */
    fun distanceTo(other: Place): Distance {
        return distanceTo(other.coordinates)
    }

    /**
     * Returns the [Distance] between this place and the given [coordinates].
     *
     * @param coordinates The target coordinates.
     */
    fun distanceTo(coordinates: Coordinates): Distance {
        return this.coordinates.distanceTo(coordinates)
    }
}
