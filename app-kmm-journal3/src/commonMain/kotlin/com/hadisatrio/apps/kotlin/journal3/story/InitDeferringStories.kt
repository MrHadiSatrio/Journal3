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
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.moment.Moments

class InitDeferringStories(
    private val provider: () -> Stories
) : Stories {

    private val origin: Stories by lazy(provider)
    override val moments: Moments get() = origin.moments

    override fun new(): EditableStory {
        return origin.new()
    }

    override fun containsStory(id: Uuid): Boolean {
        return origin.containsStory(id)
    }

    override fun findStory(id: Uuid): Iterable<Story> {
        return origin.findStory(id)
    }

    override fun hasMoments(): Boolean {
        return origin.hasMoments()
    }

    override fun containsMoment(id: Uuid): Boolean {
        return origin.containsMoment(id)
    }

    override fun findMoment(id: Uuid): Iterable<Moment> {
        return origin.findMoment(id)
    }

    override fun mostRecentMoment(): Moment {
        return origin.mostRecentMoment()
    }

    override fun iterator(): Iterator<Story> {
        return origin.iterator()
    }
}
