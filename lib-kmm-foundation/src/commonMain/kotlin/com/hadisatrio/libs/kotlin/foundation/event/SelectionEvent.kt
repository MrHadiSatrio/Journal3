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

class SelectionEvent(
    val selectionKind: String,
    val selectedIdentifier: String,
    val additionalEntries: Map<String, String>
) : Event() {

    constructor(
        selectionKind: String,
        selectedIdentifier: String,
        vararg additionalEntries: Pair<String, String>
    ) : this(selectionKind, selectedIdentifier, additionalEntries.toMap())

    override fun describeInternally(): Map<String, String> {
        val mainEntries = mapOf("selection_kind" to selectionKind, "selected_id" to selectedIdentifier)
        return additionalEntries.plus(mainEntries)
    }
}
