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

package com.hadisatrio.libs.kotlin.geography.fake

import com.benasher44.uuid.Uuid
import com.hadisatrio.libs.kotlin.geography.Place
import com.hadisatrio.libs.kotlin.geography.Places

class FakePlaces(
    private val places: MutableList<Place>
) : Places {

    constructor(vararg place: Place) : this(place.toMutableList())

    override fun new(): Place {
        val place = FakePlace()
        places.add(place)
        return place
    }

    override fun findPlace(id: Uuid): Iterable<Place> {
        return places.filter { it.id == id }
    }

    override fun iterator(): Iterator<Place> {
        return places.iterator()
    }
}
