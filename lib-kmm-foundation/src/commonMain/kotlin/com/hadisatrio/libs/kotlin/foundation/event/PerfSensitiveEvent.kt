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

package com.hadisatrio.libs.kotlin.foundation.event

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

class PerfSensitiveEvent internal constructor(
    private val tag: String,
    private val clock: Clock = Clock.System,
    private val start: PerfSensitiveEvent? = null,
) : Event() {

    private val time: Instant = clock.now()
    private val duration: Duration? = start?.let { time - it.time }

    override fun describeInternally(): Map<String, String> {
        val description = mapOf(
            "tag" to tag,
            "epoch_time" to time.toEpochMilliseconds().toString()
        )

        return if (duration == null) {
            description
        } else {
            description.plus("duration" to duration.inWholeMilliseconds.toString())
        }
    }

    internal fun end(): PerfSensitiveEvent {
        return PerfSensitiveEvent(tag, clock, this)
    }
}
