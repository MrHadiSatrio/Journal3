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

package com.hadisatrio.apps.kotlin.journal3.moment

import com.benasher44.uuid.Uuid

/**
 * A collection of [Memorable] resources that can be queried by moment association.
 */
interface Memorables : Iterable<Memorable> {

    /**
     * Records [thing] as a [Memorable] linked to the moment identified by [momentId],
     * creating the internal representation if necessary.
     */
    fun relate(momentId: Uuid, thing: Any)

    /**
     * Returns all [Memorable]s whose id matches [id].
     */
    fun find(id: Uuid): Iterable<Memorable>

    /**
     * Returns all [Memorable]s associated with the moment identified by [momentId].
     */
    fun relevantTo(momentId: Uuid): Iterable<Memorable>
}
