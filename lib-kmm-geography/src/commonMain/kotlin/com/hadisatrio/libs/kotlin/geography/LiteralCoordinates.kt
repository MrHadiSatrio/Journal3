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

class LiteralCoordinates(
    override val latitude: Double,
    override val longitude: Double
) : Coordinates() {

    constructor(string: String) : this(
        string.split(',', limit = 2).first().trim().toDouble(),
        string.split(',', limit = 2).last().trim().toDouble()
    )

    init {
        require(latitude in VALID_LAT_RANGE) {
            "The given latitude value of $latitude " +
                "is outside of the valid range (-90.0 to 90.0)."
        }
        require(longitude in VALID_LONG_RANGE) {
            "The given longitude value of $longitude " +
                "is outside of the valid range (-180.0 to 180.0)."
        }
    }

    companion object {
        private val VALID_LAT_RANGE = -90F..90F
        private val VALID_LONG_RANGE = -180F..180F
    }
}
