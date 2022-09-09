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

package com.hadisatrio.libs.android.foundation.activity

import androidx.activity.ComponentActivity
import androidx.test.runner.AndroidJUnit4
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import java.lang.IllegalStateException

@RunWith(AndroidJUnit4::class)
class CurrentActivityTest {

    @Test
    fun `Provides the currently active activity`() {
        val currentActivity = CurrentActivity(RuntimeEnvironment.getApplication())
        Robolectric.buildActivity(FooActivity::class.java).setup().visible()
        Robolectric.buildActivity(BarActivity::class.java).setup().visible()

        currentActivity.acquire().shouldBeInstanceOf<BarActivity>()
    }

    @Test
    fun `Throws when all activities are paused`() {
        val currentActivity = CurrentActivity(RuntimeEnvironment.getApplication())
        Robolectric.buildActivity(FooActivity::class.java).setup().visible().pause()
        Robolectric.buildActivity(BarActivity::class.java).setup().visible().pause()

        shouldThrow<IllegalStateException> { currentActivity.acquire() }
    }

    @Test
    fun `Throws when there are no activities at all`() {
        val currentActivity = CurrentActivity(RuntimeEnvironment.getApplication())

        shouldThrow<IllegalStateException> { currentActivity.acquire() }
    }

    private class FooActivity : ComponentActivity()

    private class BarActivity : ComponentActivity()
}
