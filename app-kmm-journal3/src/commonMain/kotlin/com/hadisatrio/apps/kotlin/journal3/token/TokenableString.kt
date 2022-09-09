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

import kotlin.jvm.JvmInline

@JvmInline
value class TokenableString(
    private val value: String
) {

    fun tokens(): Set<Token> {
        return Regex("(?<=\\s|^)([#|@][\\w_-]+)")
            .findAll(value)
            .map { Token(it.value) }
            .toSet()
    }

    override fun toString(): String {
        return value
    }

    companion object {
        val EMPTY = TokenableString("")
    }
}
