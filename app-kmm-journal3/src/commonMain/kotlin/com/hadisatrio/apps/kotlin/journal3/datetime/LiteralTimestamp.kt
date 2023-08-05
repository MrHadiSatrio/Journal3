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

package com.hadisatrio.apps.kotlin.journal3.datetime

import kotlinx.datetime.Instant

class LiteralTimestamp(
    override val value: Instant
) : Timestamp {

    constructor(iso8601: String) : this(Instant.parse(iso8601))

    constructor(epochMilliseconds: Long) : this(Instant.fromEpochMilliseconds(epochMilliseconds))

    override fun toString(): String {
        return value.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false
        other as LiteralTimestamp
        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
