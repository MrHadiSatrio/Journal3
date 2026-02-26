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

package com.hadisatrio.apps.kotlin.journal3.story

import com.hadisatrio.apps.kotlin.journal3.forgettable.Forgettable
import com.hadisatrio.apps.kotlin.journal3.moment.EditableMoment
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString

/**
 * A [Story] whose title, synopsis, and moments can be mutated.
 */
interface EditableStory : Story, Forgettable {

    /**
     * Returns `true` if this story was just created and has not yet been persisted.
     */
    fun isNewlyCreated(): Boolean

    /**
     * Updates the title of this story.
     */
    fun update(title: String)

    /**
     * Updates the synopsis of this story.
     */
    fun update(synopsis: TokenableString)

    /**
     * Creates and returns a new [EditableMoment] belonging to this story.
     */
    fun new(): EditableMoment
}
