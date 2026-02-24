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

package com.hadisatrio.libs.kotlin.collection

/**
 * An [Iterable] backed by a factory function that produces a new [Iterator] on each call.
 *
 * Useful when the same logical sequence must be traversed multiple times, with each traversal
 * starting from a fresh iterator.
 *
 * @param iteratorFactory Factory invoked on each call to [iterator].
 */
class IteratorIterable<T>(private val iteratorFactory: () -> Iterator<T>) : Iterable<T> {
    override fun iterator(): Iterator<T> = iteratorFactory()
}
