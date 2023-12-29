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

import com.badoo.reaktive.subject.publish.PublishSubject
import com.badoo.reaktive.test.observable.test
import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEvent
import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEventSource
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test

class SkippingEventSourceTest {

    @Test
    fun `Skips N elements of the given stream`() {
        val count = 10
        val origin = FakeEventSource(PublishSubject())
        val source = SkippingEventSource(count, origin)

        val events = source.events().test()
        repeat(count * 2) { origin.produce(FakeEvent()) }

        events.values.shouldHaveSize(count)
    }
}
