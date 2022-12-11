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

package com.hadisatrio.apps.android.journal3.geography

import androidx.recyclerview.widget.RecyclerView
import androidx.test.runner.AndroidJUnit4
import com.hadisatrio.apps.android.journal3.FakeJournal3Application
import com.hadisatrio.apps.android.journal3.Journal3Application
import com.hadisatrio.apps.android.journal3.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(application = FakeJournal3Application::class)
class SelectAPlaceActivityTest {

    private lateinit var application: Journal3Application
    private lateinit var activity: SelectAPlaceActivity
    private lateinit var placesListView: RecyclerView

    @Before
    fun `Starts activity`() {
        application = RuntimeEnvironment.getApplication() as Journal3Application
        activity = Robolectric.buildActivity(SelectAPlaceActivity::class.java).setup().visible().get()
        placesListView = activity.findViewById(R.id.places_list)
    }

    @Test
    fun `Presents places on its RecyclerView`() {
        val adapterItemCount = placesListView.adapter?.itemCount
        val availablePlacesCount = application.places.count()
        assert(adapterItemCount == availablePlacesCount)
    }
}
