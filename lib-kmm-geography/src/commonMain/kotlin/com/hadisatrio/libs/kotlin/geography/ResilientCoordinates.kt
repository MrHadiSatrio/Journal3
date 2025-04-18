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

@Suppress("SwallowedException")
class ResilientCoordinates(
    private val delegate: Coordinates,
    private val fallback: Coordinates
) : Coordinates() {

    override val latlng: Pair<Double, Double> get() {
        return try {
            delegate.latlng
        } catch (e: CoordinatesUnavailable) {
            fallback.latlng
        }
    }

    override val accuracyInMeters: Float get() {
        return try {
            delegate.accuracyInMeters
        } catch (e: CoordinatesUnavailable) {
            fallback.accuracyInMeters
        }
    }
}
