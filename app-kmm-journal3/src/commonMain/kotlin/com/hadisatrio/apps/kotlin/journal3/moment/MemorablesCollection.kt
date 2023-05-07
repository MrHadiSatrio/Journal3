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

class MemorablesCollection(
    private val collection: Iterable<Memorables>
) : Memorables {

    constructor(vararg memorables: Memorables) : this(memorables.toList())

    @Suppress("SwallowedException")
    override fun relate(momentId: Uuid, thing: Any) {
        var relationMade = false
        collection.forEach { memorables ->
            try {
                memorables.relate(momentId, thing)
                relationMade = true
            } catch (e: IllegalArgumentException) {
                // Do nothing as this is expected to some extent.
                // We will eventually throw in case none of the assigned memorables
                // could establish the relation accordingly.
            }
        }
        require(relationMade) {
            "Could not establish relation to ${thing::class.qualifiedName}."
        }
    }

    override fun find(id: Uuid): Iterable<Memorable> {
        val found = mutableListOf<Memorable>()
        collection.forEach { memorables -> found.addAll(memorables.find(id)) }
        return found
    }

    override fun relevantTo(momentId: Uuid): Iterable<Memorable> {
        val found = mutableListOf<Memorable>()
        collection.forEach { memorables -> found.addAll(memorables.relevantTo(momentId)) }
        return found
    }

    override fun iterator(): Iterator<Memorable> {
        return collection.fold(emptySequence<Memorable>()) { acc, m -> acc + m }.iterator()
    }
}
