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

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class Distance(
    private val one: Coordinates,
    private val other: Coordinates
) : Comparable<Distance> {

    val value: Double by lazy {
        val dLat = (other.latitude - one.latitude).toRadians()
        val dLon = (other.longitude - one.longitude).toRadians()
        val originLat = (one.latitude).toRadians()
        val destinationLat = (other.latitude).toRadians()

        val a = sin(dLat / 2).pow(2.toDouble()) +
            sin(dLon / 2).pow(2.toDouble()) *
            cos(originLat) *
            cos(destinationLat)
        val c = 2 * asin(sqrt(a))

        HAVERSINE_CONST * c * METERS_IN_KM
    }

    @Suppress("MagicNumber")
    private fun Double.toRadians(): Double {
        return this * PI / 180
    }

    override fun compareTo(other: Distance): Int {
        return value.compareTo(other.value)
    }

    override fun toString(): String {
        return "$value m"
    }

    override fun equals(other: Any?): Boolean {
        return toString().lowercase() == other.toString().lowercase()
    }

    override fun hashCode(): Int {
        return toString().hashCode()
    }

    companion object {
        private const val METERS_IN_KM = 1_000
        private const val HAVERSINE_CONST = 6372.8
    }
}
