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

import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEventSink
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class EventSinksTest {

    private val rawEventSinks = arrayOf(
        FakeEventSink(),
        FakeEventSink(),
        FakeEventSink(),
    )
    private val eventSink = EventSinks(*rawEventSinks)

    @Test
    fun `Forwards sunk events to participating sinks`() {
        eventSink.sink(CompletionEvent())
        rawEventSinks.forEach { sink -> sink.sunkCount().shouldBe(1) }
    }
}
