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

package com.hadisatrio.libs.android.foundation.widget

import android.content.Context
import android.net.Uri
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.FileProvider
import androidx.test.runner.AndroidJUnit4
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.SchedulingRxEventSource
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldStartWith
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import java.io.File

@RunWith(AndroidJUnit4::class)
@Config(shadows = [PhotoCaptureEventSourceTest.ShadowFileProvider::class])
class PhotoCaptureEventSourceTest {

    private val activityController = Robolectric.buildActivity(ComponentActivity::class.java)
    private val activity = activityController.get()
    private val triggerView = Button(activity)
    private val events = mutableListOf<Event>()
    private val scheduler = TestScheduler()

    @Test
    fun `Produces SelectionEvent upon positive activity result`() {
        val registry = immediateReturningRegistry(true)
        val source = PhotoCaptureEventSource(triggerView, activity, registry, activity.cacheDir)
        val disposable = SchedulingRxEventSource(scheduler, source).events().subscribe { events.add(it) }
        activity.setContentView(triggerView)
        activityController.setup().visible()

        triggerView.performClick()

        val description = events.first().describe()
        description.shouldContain("name" to "Selection Event")
        description.shouldContain("selection_kind" to "attachments")
        description["selected_id"].shouldNotBeNull()
        description["selected_id"].shouldStartWith("content://")
        disposable.dispose()
    }

    @Test
    fun `Does not produce SelectionEvent upon negative activity result`() {
        val registry = immediateReturningRegistry(false)
        val source = PhotoCaptureEventSource(triggerView, activity, registry, activity.cacheDir)
        val disposable = SchedulingRxEventSource(scheduler, source).events().subscribe { events.add(it) }
        activity.setContentView(triggerView)
        activityController.setup().visible()

        triggerView.performClick()

        events.shouldBeEmpty()
        disposable.dispose()
    }

    private fun immediateReturningRegistry(expectedResult: Any): ActivityResultRegistry {
        return object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, expectedResult)
            }
        }
    }

    @Suppress("UtilityClassWithPublicConstructor")
    @Implements(FileProvider::class)
    internal class ShadowFileProvider {

        companion object {

            @Suppress("UnusedParameter")
            @JvmStatic
            @Implementation
            fun getUriForFile(context: Context, authority: String, file: File): Uri {
                return Uri.parse("content://$authority/${file.name}")
            }
        }
    }
}
