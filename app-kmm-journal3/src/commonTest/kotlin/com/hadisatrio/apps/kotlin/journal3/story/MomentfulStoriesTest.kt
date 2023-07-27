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

import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test

class MomentfulStoriesTest {

    @Test
    fun `Filters out stories that has no moments`() {
        val origin = SelfPopulatingStories(noOfStories = 2, noOfMoments = 1, FakeStories())
        val momentful = MomentfulStories(origin)
        origin.first().moments.first().forget()

        momentful.shouldHaveSize(1)
    }
}
