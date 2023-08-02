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

package com.hadisatrio.apps.kotlin.journal3.datetime

import io.kotest.matchers.shouldBe
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

class FormattedTimestampTest {

    @Test
    fun `Converts itself to formatted string`() {
        val timestamp = LiteralTimestamp(718340400000) // 1992-10-06T03:00:00Z
        val formattedTimestamp1 = FormattedTimestamp("yyyy-MM-dd", timestamp)
        val formattedTimestamp2 = FormattedTimestamp("MMM dd, yyyy", timestamp)
        formattedTimestamp1.toString().shouldBe("1992-10-06")
        formattedTimestamp2.toString().shouldBe("Oct 06, 1992")
    }

    @Test
    fun `Converts itself to formatted and localized string`() {
        val timestamp = LiteralTimestamp(718340400000) // 1992-10-06T03:00:00Z
        val locale = Locale.US
        val timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        val formattedTimestamp = FormattedTimestamp(locale, timeZone, "hh:mm", timestamp)
        formattedTimestamp.toString().shouldBe("10:00")
    }
}
