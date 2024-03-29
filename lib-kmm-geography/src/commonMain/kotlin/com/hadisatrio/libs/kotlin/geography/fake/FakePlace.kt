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

package com.hadisatrio.libs.kotlin.geography.fake

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.hadisatrio.libs.kotlin.geography.Coordinates
import com.hadisatrio.libs.kotlin.geography.LiteralCoordinates
import com.hadisatrio.libs.kotlin.geography.Place
import kotlin.random.Random

@Suppress("MagicNumber")
class FakePlace(
    override val id: Uuid =
        uuid4(),
    override val name: String =
        "Fake Building ${Random.nextInt(100, 999)}",
    override val address: String =
        "${Random.nextInt(1, 999)} Fake Street, Phonytown, FK ${Random.nextInt(10000, 99999)}",
    override val coordinates: Coordinates =
        LiteralCoordinates("${Random.nextDouble(-90.0, 90.0)},${Random.nextDouble(-180.0, 180.0)}")
) : Place
