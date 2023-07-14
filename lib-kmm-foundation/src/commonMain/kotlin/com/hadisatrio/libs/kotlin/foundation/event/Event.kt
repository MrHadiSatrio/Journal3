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

package com.hadisatrio.libs.kotlin.foundation.event

abstract class Event {

    val name: String by lazy { this::class.simpleName!!.splitWordCaseApart() }

    fun describe(): Map<String, String> {
        return mapOf("name" to name) + describeInternally()
    }

    operator fun get(key: String): String? {
        return describe()[key]
    }

    protected abstract fun describeInternally(): Map<String, String>

    private fun String.splitWordCaseApart(): String {
        return this.split(WORD_CASE_SPLITTER_REGEX).joinToString(" ").trim()
    }

    fun interface Factory {
        fun create(): Event
    }

    fun interface ArgumentedFactory<T> {
        fun create(argument: T): Event
    }

    companion object {
        private val WORD_CASE_SPLITTER_REGEX by lazy { Regex("(?=\\p{Lu})") }
    }
}
