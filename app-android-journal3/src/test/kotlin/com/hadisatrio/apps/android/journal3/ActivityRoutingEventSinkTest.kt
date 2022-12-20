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

package com.hadisatrio.apps.android.journal3

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.test.runner.AndroidJUnit4
import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.android.journal3.geography.SelectAPlaceActivity
import com.hadisatrio.apps.android.journal3.story.EditAStoryActivity
import com.hadisatrio.apps.android.journal3.story.ViewStoryActivity
import com.hadisatrio.libs.android.foundation.activity.CurrentActivity
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@Ignore(
    """
        Possible bug from Robolectric prevented CurrentActivity from behaving properly on Android modules.
        This could not have been a bug from our side since CurrentActivityTest (defined within :lib-kmm-foundation)
        is green and passing. https://github.com/robolectric/robolectric/issues/7877#issue-1504049558
    """
)
@RunWith(AndroidJUnit4::class)
@Config(application = Application::class, manifest = Config.NONE)
class ActivityRoutingEventSinkTest {

    private val currentActivity = CurrentActivity(RuntimeEnvironment.getApplication())
    private val eventSink = ActivityRoutingEventSink(currentActivity)

    @Before
    fun `Starts activity`() {
        Robolectric.buildActivity(ComponentActivity::class.java).setup().visible()
    }

    @Test(timeout = 5_000)
    fun `Starts EditAStoryActivity post receiving 'action' selection of 'add_story'`() {
        eventSink.sink(SelectionEvent("action", "add_story"))
        currentActivity.acquire().shouldBeInstanceOf<EditAStoryActivity>()
    }

    @Test(timeout = 5_000)
    fun `Starts SelectAPlaceActivity post receiving 'action' selection of 'select_place'`() {
        eventSink.sink(SelectionEvent("action", "select_place"))
        currentActivity.acquire().shouldBeInstanceOf<SelectAPlaceActivity>()
    }

    @Test(timeout = 5_000)
    fun `Starts ViewStoryActivity post receiving valid 'action' selection of 'view_story'`() {
        val storyId = uuid4().toString()
        eventSink.sink(SelectionEvent("action", "view_story?id=$storyId"))

        val startedActivity = currentActivity.acquire()
        startedActivity.shouldBeInstanceOf<ViewStoryActivity>()
        val intentExtras = startedActivity.intent.extras
        intentExtras.shouldNotBeNull()
        intentExtras.getString("target_id").shouldBe(storyId)
    }

    @Test(timeout = 5_000)
    fun `Throws IllegalArgumentException post receiving invalid 'action' selection of 'view_story'`() {
        shouldThrow<IllegalArgumentException> {
            eventSink.sink(SelectionEvent("action", "view_story"))
        }
    }
}
