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

class DescriptionParaphrasingMoment(
    private val paraphraser: Paraphraser,
    private val origin: MomentInEdit
) : MomentInEdit by origin {

    override fun commit() {
        val original = origin.description.toString()
        val paraphrased = paraphraser.paraphrase(original)
        origin.update(TokenableString(paraphrased))
        origin.commit()
    }
}
