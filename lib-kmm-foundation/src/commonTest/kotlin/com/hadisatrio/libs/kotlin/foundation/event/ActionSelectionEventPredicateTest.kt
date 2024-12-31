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

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.test.Test

class ActionSelectionEventPredicateTest {

    @Test
    fun `Matches only action selection events with the given identifier`() {
        val identifiers = setOf("one", "another")
        val events = identifiers.map { SelectionEvent("action", it) }

        val predicate = ActionSelectionEventPredicate(identifiers)

        events.forEach { event -> predicate.applicable(event).shouldBeTrue() }
        predicate.applicable(SelectionEvent("action", "unknown")).shouldBeFalse()
        predicate.applicable(SelectionEvent("not_action", "one")).shouldBeFalse()
    }
}
