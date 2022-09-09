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

package com.hadisatrio.apps.kotlin.journal3.token

class Token(raw: String) {

    private val value: String = raw

    init {
        require(raw.isNotBlank()) {
            "Tokens may not be a blank string."
        }
        require(!raw.contains(' ')) {
            "Tokens may not consist more than 1 word."
        }
        require(PREFIXES.contains(raw[0])) {
            "Tokens may only start with either of these characters: $PREFIXES."
        }
    }

    override fun toString(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Token) return false
        if (value != other.value) return false
        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    companion object {

        private const val PREFIX_PEOPLE: Char = '@'
        private const val PREFIX_TOPIC: Char = '#'

        private val PREFIXES: Set<Char> = setOf(PREFIX_PEOPLE, PREFIX_TOPIC)
    }
}
