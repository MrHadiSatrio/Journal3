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
import com.hadisatrio.apps.kotlin.journal3.story.Story
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStory
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlin.test.Test

class CachingStoryPresenterTest {

    @Test
    fun `Forwards cached story to the origin`() {
        val story = spyk(FakeStory(uuid4(), mutableListOf()))
        val origin = mockk<Presenter<Story>>(relaxed = true)

        CachingStoryPresenter(origin = origin).present(story)

        verify {
            origin.present(
                withArg { cachedStory ->
                    repeat(10) { cachedStory.title }
                    verify(exactly = 1) { story.title }
                }
            )
        }
    }
}
