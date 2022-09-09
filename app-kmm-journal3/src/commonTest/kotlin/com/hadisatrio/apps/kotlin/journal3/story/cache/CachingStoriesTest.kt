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

package com.hadisatrio.apps.kotlin.journal3.story.cache

import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.kotlin.journal3.story.FakeStories
import com.hadisatrio.apps.kotlin.journal3.story.FakeStory
import com.hadisatrio.apps.kotlin.journal3.story.Story
import io.mockk.spyk
import io.mockk.verify
import kotlin.test.Test

class CachingStoriesTest {

    @Test
    fun `Prevents multiple property access to the original stories`() {
        val rawStories = mutableListOf<Story>()
        val story = spyk(FakeStory(uuid4(), rawStories))
        val origin = FakeStories(story)
        val decorated = CachingStories(origin)

        val cachedStory = decorated.first()
        repeat(times = 10) { cachedStory.id }
        repeat(times = 10) { cachedStory.title }
        repeat(times = 10) { cachedStory.synopsis }

        verify(exactly = 1) { story.id }
        verify(exactly = 1) { story.title }
        verify(exactly = 1) { story.synopsis }
    }
}
