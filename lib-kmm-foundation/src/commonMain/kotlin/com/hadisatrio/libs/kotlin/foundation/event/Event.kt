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

/**
 * The base type for every domain event circulating in the system.
 *
 * Provides a human-readable [name] derived from the concrete class name and a structured
 * key-value description via [describe], suitable for logging and analytics.
 */
abstract class Event {

    val name: String by lazy { this::class.simpleName!!.splitWordCaseApart() }

    /**
     * Returns a map describing this event, including its [name] and any subclass-specific fields.
     */
    fun describe(): Map<String, String> {
        return mapOf("name" to name) + describeInternally()
    }

    /**
     * Returns the value mapped to [key] in [describe], or `null` if absent.
     *
     * @param key Key to look up in the event description.
     */
    operator fun get(key: String): String? {
        return describe()[key]
    }

    protected abstract fun describeInternally(): Map<String, String>

    private fun String.splitWordCaseApart(): String {
        return this.split(WORD_CASE_SPLITTER_REGEX).joinToString(" ").trim()
    }

    /**
     * A no-argument factory for [Event]s.
     */
    fun interface Factory {
        fun create(): Event
    }

    /**
     * A single-argument factory for [Event]s.
     */
    fun interface ArgumentedFactory<T> {
        /**
         * Creates an event using [argument].
         *
         * @param argument Input used to construct the event.
         */
        fun create(argument: T): Event
    }

    companion object {
        private val WORD_CASE_SPLITTER_REGEX by lazy { Regex("(?=\\p{Lu})") }
    }
}
