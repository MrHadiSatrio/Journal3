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

package com.hadisatrio.libs.kotlin.foundation.modal

import io.kotest.matchers.maps.shouldContain
import kotlin.test.Test

class BinaryConfirmationModalTest {

    @Test
    fun `Provides a factory to create approval events`() {
        val kind = "foo"

        val modal = BinaryConfirmationModal(kind)

        modal.positiveEventFactory.create().describe().shouldContain("modal_kind" to kind)
        modal.negativeEventFactory.create().describe().shouldContain("modal_kind" to kind)
    }
}
