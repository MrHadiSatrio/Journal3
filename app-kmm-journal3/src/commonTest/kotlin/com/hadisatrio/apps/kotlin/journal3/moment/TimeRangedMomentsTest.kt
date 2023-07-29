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

import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.story.SelfPopulatingStories
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import io.kotest.matchers.booleans.shouldBeTrue
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.days

class TimeRangedMomentsTest {

    private val currentInstant: Instant = Clock.System.now()
    private val origin: Moments = SelfPopulatingStories(noOfStories = 1, noOfMoments = 10, FakeStories()).moments
    private val timeRange: ClosedRange<Timestamp> = Timestamp(currentInstant - 3.days)..Timestamp(currentInstant)
    private val moments: TimeRangedMoments = TimeRangedMoments(timeRange, origin)

    @BeforeTest
    fun `Init moments`() {
        origin.forEachIndexed { index, moment ->
            moment.update(Timestamp(currentInstant - index.days))
        }
    }

    @Test
    fun `Filters out moments whose timestamp is out of range`() {
        val sentimentalIds = moments.map { it.id }.toList()
        sentimentalIds.forEach { id ->
            val moment = origin.find(id).first()
            (moment.timestamp in timeRange).shouldBeTrue()
        }
    }
}
