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
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStory
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import kotlin.test.Test

class CachingStoriesPresenterTest {

    @Test
    fun `Forwards cached stories to the origin`() {
        val story = spyk(FakeStory(uuid4(), mutableListOf()))
        val stories = FakeStories(story)
        val origin = mockk<Presenter<Stories>>(relaxed = true)
        val presented = slot<Stories>()
        every { origin.present(capture(presented)) } answers { nothing }

        CachingStoriesPresenter(origin = origin).present(stories)

        val cached = presented.captured.first()
        repeat(10) { cached.title }
        verify(exactly = 1) { story.title }
    }
}
