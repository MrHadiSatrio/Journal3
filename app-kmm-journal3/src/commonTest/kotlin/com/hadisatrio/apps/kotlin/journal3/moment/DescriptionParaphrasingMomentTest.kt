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

package com.hadisatrio.apps.kotlin.journal3.moment

import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.kotlin.paraphrase.Paraphraser
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class DescriptionParaphrasingMomentTest {

    private val paraphraser: Paraphraser = mockk(relaxed = true)
    private val origin: MomentInEdit = mockk(relaxed = true)
    private val moment: DescriptionParaphrasingMoment = DescriptionParaphrasingMoment(paraphraser, origin)

    @Test
    fun `Updates the origin's description with the paraphrased string upon commit`() {
        every { origin.description }.returns(TokenableString("foo"))
        every { paraphraser.paraphrase("foo") }.returns("bar")
        moment.commit()
        verify { paraphraser.paraphrase("foo") }
        verify { origin.update(TokenableString("bar")) }
        verify { origin.commit() }
    }
}
