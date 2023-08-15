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

import com.benasher44.uuid.uuid4
import com.hadisatrio.libs.kotlin.geography.LiteralCoordinates
import com.hadisatrio.libs.kotlin.geography.fake.FakePlace
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import org.junit.Test

class PlaceItemDifferTest {

    @Test
    fun `Reports two places with differing IDs as being different`() {
        val one = FakePlace(uuid4())
        val another = FakePlace(uuid4())

        PlaceItemDiffer.areItemsTheSame(one, another).shouldBeFalse()
    }

    @Test
    fun `Reports two places with the same ID as being same`() {
        val id = uuid4()
        val one = FakePlace(id)
        val another = FakePlace(id)

        PlaceItemDiffer.areItemsTheSame(one, another).shouldBeTrue()
    }

    @Test
    fun `Reports two places with identical content as being same content-wise`() {
        val id = uuid4()
        val name = "Foo"
        val address = "Bar"
        val coordinates = LiteralCoordinates("-7.607355,110.203804")
        val one = FakePlace(id, name, address, coordinates)
        val another = FakePlace(id, name, address, coordinates)

        PlaceItemDiffer.areContentsTheSame(one, another).shouldBeTrue()
    }

    @Test
    fun `Reports two places with differing name as being different content-wise`() {
        val id = uuid4()
        val one = FakePlace(id = id, name = "Foo")
        val another = FakePlace(id = id, name = "Bar")

        PlaceItemDiffer.areContentsTheSame(one, another).shouldBeFalse()
    }

    @Test
    fun `Reports two places with differing address as being different content-wise`() {
        val id = uuid4()
        val one = FakePlace(id = id, address = "Foo")
        val another = FakePlace(id = id, address = "Bar")

        PlaceItemDiffer.areContentsTheSame(one, another).shouldBeFalse()
    }

    @Test
    fun `Reports two places with differing coordinates as being different content-wise`() {
        val id = uuid4()
        val one = FakePlace(id = id, coordinates = LiteralCoordinates("-7.607355,110.203804"))
        val another = FakePlace(id = id, coordinates = LiteralCoordinates("37.5323749,-122.4151854"))

        PlaceItemDiffer.areContentsTheSame(one, another).shouldBeFalse()
    }
}
