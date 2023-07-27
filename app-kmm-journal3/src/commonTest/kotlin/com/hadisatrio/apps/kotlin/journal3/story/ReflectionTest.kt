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

package com.hadisatrio.apps.kotlin.journal3.story

import com.benasher44.uuid.Uuid
import com.hadisatrio.apps.kotlin.journal3.moment.fake.FakeMoments
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import kotlin.test.Test
import kotlin.test.assertEquals

class ReflectionTest {

    @Test
    fun `Infers its ID on basis of the given title`() {
        val title = "Test Reflection"
        val synopsis = TokenableString("This is a test reflection.")
        val moments = FakeMoments()
        val reflection = Reflection(title, synopsis, moments)

        assertEquals(Uuid.nameUUIDFromBytes(title.toByteArray()), reflection.id)
    }

    @Test
    fun `Compares itself to other stories on basis of title`() {
        val title1 = "Test Reflection 1"
        val title2 = "Test Reflection 2"
        val synopsis = TokenableString("This is a test reflection.")
        val moments = FakeMoments()
        val reflection1 = Reflection(title1, synopsis, moments)
        val reflection2 = Reflection(title2, synopsis, moments)

        assertEquals(-1, reflection1.compareTo(reflection2))
        assertEquals(1, reflection2.compareTo(reflection1))
        assertEquals(0, reflection1.compareTo(reflection1))
    }
}
