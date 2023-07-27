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

import com.benasher44.uuid.Uuid
import com.hadisatrio.apps.kotlin.journal3.story.SelfPopulatingStories
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class CountLimitingMomentsTest {

    private val origin: Moments = SelfPopulatingStories(noOfStories = 1, noOfMoments = 10, FakeStories()).moments
    private val moments: CountLimitingMoments = CountLimitingMoments(limit = 3, origin)
    private val originalIds: List<Uuid> = origin.map { it.id }.toList()
    private val truncatedIds: List<Uuid> = moments.map { it.id }.toList()

    @Test
    fun `Truncate the number of moments according to the given limit`() {
        moments.count().shouldBe(3)
        truncatedIds.shouldHaveSize(3)
        truncatedIds.forEachIndexed { index, id -> originalIds[index].shouldBeEqual(id) }
    }

    @Test
    fun `Finds nothing when asked to look for moments that have been truncated`() {
        originalIds.take(3).forEach { moments.find(it).shouldNotBeEmpty() }
        originalIds.drop(3).forEach { moments.find(it).shouldBeEmpty() }
    }
}
