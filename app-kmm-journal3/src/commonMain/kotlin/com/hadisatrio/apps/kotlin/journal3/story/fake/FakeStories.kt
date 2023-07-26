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

package com.hadisatrio.apps.kotlin.journal3.story.fake

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.kotlin.journal3.moment.MergedMoments
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.moment.Moments
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.apps.kotlin.journal3.story.Story

class FakeStories(
    private val stories: MutableList<Story>
) : Stories {

    override val moments: Moments get() = MergedMoments(map { it.moments })

    constructor(vararg stories: Story) : this(stories.toMutableList())

    override fun new(): Story {
        val story = FakeStory(uuid4(), stories)
        stories.add(story)
        return story
    }

    override fun containsStory(id: Uuid): Boolean {
        return findStory(id).count() > 0
    }

    override fun findStory(id: Uuid): Iterable<Story> {
        return filter { it.id == id }
    }

    override fun hasMoments(): Boolean {
        return any { story -> story.moments.count() > 0 }
    }

    override fun containsMoment(id: Uuid): Boolean {
        return findMoment(id).count() > 0
    }

    override fun findMoment(id: Uuid): Iterable<Moment> {
        return flatMap { it.moments.find(id) }
    }

    override fun mostRecentMoment(): Moment {
        val recentMoments = mutableListOf<Moment>()
        forEach { story ->
            if (!story.moments.iterator().hasNext()) return@forEach
            recentMoments.add(story.moments.mostRecent())
        }
        return recentMoments.maxBy { it.timestamp }
    }

    override fun iterator(): Iterator<Story> {
        return stories.iterator()
    }
}
