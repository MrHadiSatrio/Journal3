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

package com.hadisatrio.apps.android.journal3.story

import com.hadisatrio.apps.kotlin.journal3.story.Story
import com.hadisatrio.libs.kotlin.foundation.presentation.Adapter

class StoryStringAdapter(
    private val key: String
) : Adapter<Story, String> {

    init {
        require(key in SUPPORTED_KEYS) {
            "Expected key to be either ${SUPPORTED_KEYS.contentToString()} but was $key."
        }
    }

    override fun adapt(thing: Story): String {
        return when (key) {
            "title" -> thing.title
            "synopsis" -> thing.synopsis.toString()
            "attachment_count" -> thing.moments.count().toString() + " attachment(s)"
            else -> throw UnsupportedOperationException("Unknown key '$key'.")
        }
    }

    companion object {
        private val SUPPORTED_KEYS = arrayOf("title", "synopsis", "attachment_count")
    }
}
