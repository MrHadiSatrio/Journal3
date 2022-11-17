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

abstract class Coordinates {

    abstract val latitude: Double
    abstract val longitude: Double

    fun distanceTo(other: Coordinates): Distance {
        return Distance(this, other)
    }

    final override fun toString(): String {
        return "$latitude,$longitude"
    }

    final override fun equals(other: Any?): Boolean {
        return toString() == other.toString()
    }

    final override fun hashCode(): Int {
        return toString().hashCode()
    }
}
