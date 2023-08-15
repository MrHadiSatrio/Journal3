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

import androidx.recyclerview.widget.RecyclerView
import androidx.test.runner.AndroidJUnit4
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment

@RunWith(AndroidJUnit4::class)
class RecyclerViewPresenterTest {

    private val recyclerView = spyk(RecyclerView(RuntimeEnvironment.getApplication()))
    private val strings = listOf("Foo", "Bar", "Fizz", "Buzz")
    private val presenter = RecyclerViewPresenter<String>(recyclerView)

    @Test
    fun `Renders the given strings onto the RecyclerView`() {
        presenter.present(strings)
        recyclerView.layoutManager.shouldNotBeNull()
        recyclerView.adapter.shouldNotBeNull()
        recyclerView.adapter!!.itemCount.shouldBe(strings.size)
    }

    @Test
    fun `Shares the adapter and layout manager for all presentation calls`() {
        repeat(10) { presenter.present(strings) }
        verify(exactly = 1) { recyclerView.setAdapter(any()) }
        verify(exactly = 1) { recyclerView.setLayoutManager(any()) }
    }
}
