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

package com.hadisatrio.libs.android.foundation.widget.recyclerview

import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.test.runner.AndroidJUnit4
import com.hadisatrio.libs.android.foundation.concurrent.CurrentThreadExecutor
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class ListViewPresenterTest {

    private val activityController = Robolectric.buildActivity(ComponentActivity::class.java)
    private val activity = activityController.get()
    private val recyclerView = RecyclerView(activity)
    private val strings = mutableListOf<String>().apply { repeat(100) { add("Foo") } }
    private val presenter = ListViewPresenter<String>(recyclerView, backgroundExecutor = CurrentThreadExecutor())

    @Before
    fun `Starts activity`() {
        activity.setContentView(recyclerView)
        activityController.setup()
    }

    @After
    fun `Destroys activity`() {
        activityController.destroy()
    }

    @Test
    fun `Renders the given items onto the RecyclerView, twenty at a time`() {
        val looper = shadowOf(Looper.getMainLooper())

        presenter.present(strings)

        recyclerView.layoutManager.shouldNotBeNull()
        recyclerView.adapter.shouldNotBeNull()
        looper.idle()
        recyclerView.adapter!!.itemCount.shouldBe(20)

        recyclerView.scrollBy(Int.MAX_VALUE, Int.MAX_VALUE)
        looper.idle()
        recyclerView.adapter!!.itemCount.shouldBeGreaterThan(20)
    }
}
