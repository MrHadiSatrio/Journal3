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

package com.hadisatrio.apps.kotlin.journal3.moment.fake

import com.benasher44.uuid.Uuid
import com.hadisatrio.apps.kotlin.journal3.moment.Memorable
import com.hadisatrio.apps.kotlin.journal3.moment.Memorables

class FakeMemorables(
    private val acceptedKind: String
) : Memorables {

    private val memorables = mutableListOf<Memorable>()

    override fun relate(momentId: Uuid, thing: Any) {
        require(thing is FakeMemorable && thing.kind == acceptedKind) {
            "Could not establish relation to ${thing::class.qualifiedName}."
        }

        val old = relevantTo(momentId).firstOrNull()
        val new = memorables.find { it == thing } ?: thing.also { memorables.add(it) }

        old?.unlink(momentId)
        new.link(momentId)
    }

    override fun find(id: Uuid): Iterable<Memorable> {
        return memorables.filter { it.id == id }
    }

    override fun relevantTo(momentId: Uuid): Iterable<Memorable> {
        return memorables.filter { it.relevantTo(momentId) }
    }

    override fun iterator(): Iterator<Memorable> {
        return memorables.iterator()
    }
}
