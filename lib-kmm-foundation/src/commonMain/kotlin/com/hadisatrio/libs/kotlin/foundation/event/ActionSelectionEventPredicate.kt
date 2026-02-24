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
 * A [Predicate] that matches [SelectionEvent]s of kind "action" whose
 * [SelectionEvent.selectedIdentifier] is in the given set.
 *
 * @param identifiers Set of action identifiers to match against.
 */
class ActionSelectionEventPredicate(
    private val identifiers: Set<String>
) : Predicate<Event> {

    constructor(vararg identifiers: String) : this(identifiers.toSet())

    override fun applicable(thing: Event): Boolean {
        val selection = (thing as? SelectionEvent) ?: return false
        val isAction = selection.selectionKind == "action"
        val idMatches = selection.selectedIdentifier in identifiers
        return isAction && idMatches
    }
}
