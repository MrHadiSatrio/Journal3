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

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FormattedTimestamp(
    private val locale: Locale,
    private val timeZone: TimeZone,
    private val format: String,
    private val origin: Timestamp
) : Timestamp by origin {

    private val formatter = ThreadLocal<SimpleDateFormat>()

    constructor(format: String, origin: Timestamp) : this(Locale.getDefault(), TimeZone.getDefault(), format, origin)

    override fun toString(): String {
        val localFormatter = formatter.get() ?: SimpleDateFormat(format, locale)
        localFormatter.timeZone = timeZone
        formatter.set(localFormatter)
        return localFormatter.format(Date(value.toEpochMilliseconds()))
    }
}
