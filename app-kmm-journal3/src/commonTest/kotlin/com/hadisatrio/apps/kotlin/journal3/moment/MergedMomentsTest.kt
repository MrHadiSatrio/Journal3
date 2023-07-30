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

package com.hadisatrio.apps.kotlin.journal3.moment

import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import kotlin.test.BeforeTest
import kotlin.test.Test

class MergedMomentsTest {

    private val firstStory = FakeStories().new()
    private val secondStory = FakeStories().new()
    private val merged = MergedMoments(firstStory.moments, secondStory.moments)

    @BeforeTest
    fun `Populate moments`() {
        repeat(10) {
            firstStory.new()
            secondStory.new()
        }
    }

    @Test
    fun `Counts all moments in the merged collection`() {
        merged.count().shouldBe(20)
    }

    @Test
    fun `Finds moments in the merged collection`() {
        val firstId = firstStory.moments.shuffled().first().id
        val secondId = secondStory.moments.shuffled().first().id
        val invalidId = uuid4()
        merged.find(firstId).shouldNotBeEmpty()
        merged.find(secondId).shouldNotBeEmpty()
        merged.find(invalidId).shouldBeEmpty()
    }

    @Test
    fun `Finds the most recent moment over the merged collection`() {
        val mostRecent = (firstStory.moments + secondStory.moments).maxBy { it.timestamp }
        merged.mostRecent().id.shouldBe(mostRecent.id)
    }

    @Test
    fun `Iterates over all moments in the merged collection`() {
        merged.shouldHaveSize(20)
    }
}
