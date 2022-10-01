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

package com.hadisatrio.apps.kotlin.journal3.sentiment

import kotlin.jvm.JvmInline

@JvmInline
value class Sentiment(val value: Float) {

    constructor(value: String) : this(value.toFloat())

    constructor(others: Iterable<Sentiment>) : this(others.map { it.value }.average().toFloat())

    init {
        require(value in 0.0F..1.0F) {
            "Sentiment value must be between 0.0 and 1.0; given was $value."
        }
    }

    override fun toString(): String {
        return value.toString()
    }

    companion object {
        val DEFAULT = Sentiment(0.123456789F)
    }
}
