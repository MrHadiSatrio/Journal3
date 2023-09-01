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

import com.benasher44.uuid.Uuid
import com.hadisatrio.apps.kotlin.journal3.moment.EditableMoment
import com.hadisatrio.apps.kotlin.journal3.moment.Moments
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString

class EditableStoryInStories(
    private val storyId: Uuid,
    private val stories: Stories
) : EditableStory {

    private val origin: EditableStory by lazy {
        if (storyId.isValid()) {
            stories.findStory(storyId).first() as EditableStory
        } else {
            stories.new()
        }
    }

    override val id: Uuid get() = origin.id
    override val title: String get() = origin.title
    override val synopsis: TokenableString get() = origin.synopsis
    override val moments: Moments get() = origin.moments

    override fun isNewlyCreated(): Boolean = origin.isNewlyCreated()
    override fun update(title: String) = origin.update(title)
    override fun update(synopsis: TokenableString) = origin.update(synopsis)
    override fun new(): EditableMoment = origin.new()
    override fun compareTo(other: Story): Int = origin.compareTo(other)
    override fun forget() = origin.forget()

    private fun Uuid.isValid(): Boolean {
        return this.toString() != "00000000-0000-0000-0000-000000000000"
    }
}
