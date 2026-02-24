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

import com.benasher44.uuid.Uuid
import com.hadisatrio.libs.kotlin.geography.Coordinates
import com.hadisatrio.libs.kotlin.geography.LiteralCoordinates
import com.hadisatrio.libs.kotlin.geography.Place
import com.google.android.libraries.places.api.model.Place as GooglePlace

/**
 * A [Place] backed by a [com.google.android.libraries.places.api.model.Place] from the Google
 * Places SDK.
 *
 * @param backing The underlying Google SDK place object.
 */
class GoogleSdkPlace(private val backing: GooglePlace) : Place {

    override val id: Uuid by lazy {
        val bytes = backing.id.toByteArray()
        Uuid.nameUUIDFromBytes(bytes)
    }

    override val name: String by lazy { backing.name }

    override val address: String by lazy { backing.address }

    override val coordinates: Coordinates by lazy { LiteralCoordinates(backing.latLng.toString()) }

    override fun equals(other: Any?): Boolean {
        if (other !is GoogleSdkPlace) return false
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
