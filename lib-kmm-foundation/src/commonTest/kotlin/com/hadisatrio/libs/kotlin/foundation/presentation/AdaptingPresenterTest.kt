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

package com.hadisatrio.libs.kotlin.foundation.presentation

import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class AdaptingPresenterTest {

    @Test
    fun `Adapts and forwards the value to the origin`() {
        val origin = mockk<Presenter<String>>(relaxed = true)

        val presenter = AdaptingPresenter<Int, String>(
            origin = origin,
            adapter = { int -> int.toString() }
        )
        presenter.present(Int.MAX_VALUE)

        verify(exactly = 1) { origin.present(Int.MAX_VALUE.toString()) }
    }
}
