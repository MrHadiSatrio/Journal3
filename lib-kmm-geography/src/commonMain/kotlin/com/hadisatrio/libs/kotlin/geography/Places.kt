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
 * A collection of [Place]s supporting creation and lookup by [Uuid] or name.
 */
interface Places : Iterable<Place> {
    /**
     * Creates and persists a new [Place] in this collection.
     *
     * @return The newly created [Place].
     */
    fun new(): Place

    /**
     * Returns all [Place]s matching [id].
     *
     * @param id The unique identifier to search for.
     * @return Matching places, or an empty iterable if none found.
     */
    fun findPlace(id: Uuid): Iterable<Place>

    /**
     * Returns all [Place]s whose name matches [name].
     *
     * @param name The name to search for.
     * @return Matching places, or an empty iterable if none found.
     */
    fun findPlace(name: String): Iterable<Place>
}
