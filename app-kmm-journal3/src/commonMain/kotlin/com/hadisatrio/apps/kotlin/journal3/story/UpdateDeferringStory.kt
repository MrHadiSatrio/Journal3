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

import com.hadisatrio.apps.kotlin.journal3.token.TokenableString

class UpdateDeferringStory(
    private val origin: EditableStory
) : StoryInEdit, EditableStory by origin {

    private var titleInEdit: String = origin.title
    private var synopsisInEdit: TokenableString = origin.synopsis

    override val title: String get() = titleInEdit
    override val synopsis: TokenableString get() = synopsisInEdit

    override fun update(title: String) {
        this.titleInEdit = title
    }

    override fun update(synopsis: TokenableString) {
        this.synopsisInEdit = synopsis
    }

    override fun updatesMade(): Boolean {
        return titleInEdit != origin.title || synopsisInEdit != origin.synopsis
    }

    override fun commit() {
        origin.update(titleInEdit)
        origin.update(synopsisInEdit)
    }
}
