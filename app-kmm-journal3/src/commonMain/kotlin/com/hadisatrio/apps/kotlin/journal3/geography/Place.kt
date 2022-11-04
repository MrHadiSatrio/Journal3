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

package com.hadisatrio.apps.kotlin.journal3.geography

import com.benasher44.uuid.Uuid

interface Place {
    val id: Uuid
    val name: String
    val address: String
    val coordinates: Coordinates

    fun distanceTo(other: Place): Distance {
        return distanceTo(other.coordinates)
    }

    fun distanceTo(coordinates: Coordinates): Distance {
        return this.coordinates.distanceTo(coordinates)
    }
}
