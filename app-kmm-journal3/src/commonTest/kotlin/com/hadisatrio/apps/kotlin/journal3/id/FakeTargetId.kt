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

package com.hadisatrio.apps.kotlin.journal3.id

import com.benasher44.uuid.Uuid

class FakeTargetId(
    private val uuid: Uuid
) : TargetId {

    override fun asUuid(): Uuid {
        return uuid
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FakeTargetId) return false
        if (uuid != other.uuid) return false
        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}
