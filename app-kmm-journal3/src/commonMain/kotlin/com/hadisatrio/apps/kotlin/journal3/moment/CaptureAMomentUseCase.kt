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

import com.hadisatrio.apps.kotlin.journal3.datetime.LiteralTimestamp
import com.hadisatrio.apps.kotlin.journal3.id.INVALID_UUID
import com.hadisatrio.apps.kotlin.journal3.story.EditableMomentInStory
import com.hadisatrio.apps.kotlin.journal3.story.Story
import com.hadisatrio.apps.kotlin.journal3.story.cache.CachingStory
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.geography.Places
import com.hadisatrio.libs.kotlin.geography.Speed
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours

class CaptureAMomentUseCase(
    private val story: Story,
    private val places: Places,
    private val speed: Speed,
    private val clock: Clock
) : UseCase {

    override fun invoke() {
        if (speed.value > SPEED_LIMIT_METER_PER_SECOND) return

        val cachedStory = CachingStory(story)

        val currentTimestamp = LiteralTimestamp(clock.now())
        val last24h = (currentTimestamp - 24.hours)..currentTimestamp
        val todaysMoments = TimeRangedMoments(last24h, cachedStory.moments)

        var place = places.first()
        val vicinityMoments = VicinityMoments(place.coordinates, DISTANCE_LIMIT_METER, cachedStory.moments)
        place = vicinityMoments.firstOrNull()?.place ?: place

        val beenHereRecently = todaysMoments.any { it.place.id == place.id }
        if (beenHereRecently) return

        val moment = EditableMomentInStory(INVALID_UUID, story)
        moment.update(currentTimestamp)
        moment.update(isNotable = false)
        moment.update(place)
        moment.commit()
    }

    companion object {
        private const val SPEED_LIMIT_METER_PER_SECOND = 5
        private const val DISTANCE_LIMIT_METER = 25.0
    }
}
