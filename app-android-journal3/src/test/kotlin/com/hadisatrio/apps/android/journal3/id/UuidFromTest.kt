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

package com.hadisatrio.apps.android.journal3.id

import android.content.Intent
import androidx.test.runner.AndroidJUnit4
import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.kotlin.journal3.id.INVALID_UUID
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UuidFromTest {

    @Test
    fun `Gets a valid UUID when there exist a valid value in the bundle`() {
        val uuid = uuid4()
        val intent = Intent()
        intent.putExtra("foo", uuid.toString())

        intent.getUuidExtra("foo").shouldBe(uuid)
    }

    @Test
    fun `Reverts into an invalid UUID when there is no valid values in the bundle`() {
        val uuid = uuid4()
        val intent = Intent()
        intent.putExtra("foo", uuid)

        intent.getUuidExtra("foo").shouldBe(INVALID_UUID)
    }

    @Test
    fun `Reverts into an invalid UUID when there is no values in the bundle`() {
        Intent().getUuidExtra("foo").shouldBe(INVALID_UUID)
    }
}
