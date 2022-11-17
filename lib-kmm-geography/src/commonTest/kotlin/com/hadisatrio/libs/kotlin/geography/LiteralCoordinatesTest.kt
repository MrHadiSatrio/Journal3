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

package com.hadisatrio.libs.kotlin.geography

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.lang.IllegalArgumentException
import kotlin.test.Test

class LiteralCoordinatesTest {

    @Test
    fun `Tells the distance to other coordinates`() {
        val googleplex = LiteralCoordinates("37.5323749,-122.4151854")
        val applePark = LiteralCoordinates("37.562126,-122.2475523")
        googleplex.distanceTo(applePark).shouldBe("15148.717232794352 m")
    }

    @Test
    fun `Guards against invalid geographical coordinates`() {
        shouldThrow<IllegalArgumentException> { LiteralCoordinates("-180.0,180.0") }
        shouldThrow<IllegalArgumentException> { LiteralCoordinates("180.0,180.0") }
        shouldThrow<IllegalArgumentException> { LiteralCoordinates("90.0,-270.0") }
        shouldThrow<IllegalArgumentException> { LiteralCoordinates("90.0,270.0") }
    }

    @Test
    fun `Prints out correct string representation`() {
        val googleplex = LiteralCoordinates(37.5323749, -122.4151854)
        googleplex.toString().shouldBe("37.5323749,-122.4151854")
    }

    @Test
    fun `Treats two identical coordinates as equal`() {
        val googleplex = LiteralCoordinates("37.5323749,-122.4151854")
        val applePark = LiteralCoordinates("37.562126,-122.2475523")
        googleplex.shouldBe(googleplex)
        googleplex.shouldBe("37.5323749,-122.4151854")
        googleplex.hashCode().shouldBe(googleplex.hashCode())
        googleplex.hashCode().shouldBe("37.5323749,-122.4151854".hashCode())
        googleplex.shouldNotBe(applePark)
        googleplex.hashCode().shouldNotBe(applePark.hashCode())
    }
}
