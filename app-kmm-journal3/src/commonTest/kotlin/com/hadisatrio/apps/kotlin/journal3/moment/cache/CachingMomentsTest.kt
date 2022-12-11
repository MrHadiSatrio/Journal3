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

package com.hadisatrio.apps.kotlin.journal3.moment.cache

import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.moment.fake.FakeMoment
import com.hadisatrio.apps.kotlin.journal3.moment.fake.FakeMoments
import io.mockk.spyk
import io.mockk.verify
import kotlin.test.Test

class CachingMomentsTest {

    @Test
    fun `Prevents multiple property access to the original moments`() {
        val rawMoments = mutableListOf<Moment>()
        val moment = spyk(FakeMoment(uuid4(), rawMoments))
        val origin = FakeMoments(moment)
        val decorated = CachingMoments(origin)

        val cachedMoment = decorated.first()
        repeat(times = 10) { cachedMoment.id }
        repeat(times = 10) { cachedMoment.timestamp }
        repeat(times = 10) { cachedMoment.description }
        repeat(times = 10) { cachedMoment.sentiment }
        repeat(times = 10) { cachedMoment.impliedSentiment }

        verify(exactly = 1) { moment.id }
        verify(exactly = 1) { moment.timestamp }
        verify(exactly = 1) { moment.description }
        verify(exactly = 1) { moment.sentiment }
        verify(exactly = 1) { moment.impliedSentiment }
    }
}
