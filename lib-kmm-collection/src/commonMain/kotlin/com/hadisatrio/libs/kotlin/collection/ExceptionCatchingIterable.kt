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
 * An [Iterable] that routes exceptions thrown during `hasNext()` to a handler
 * instead of propagating them to the caller.
 *
 * When an exception is caught the iterator reports `false` for `hasNext()`, ending
 * iteration gracefully.
 *
 * @param handler Receives any exception thrown by the underlying iterator's `hasNext()`.
 * @param origin The wrapped iterable.
 */
class ExceptionCatchingIterable<T>(
    private val handler: (Exception) -> Unit,
    private val origin: Iterable<T>
) : Iterable<T> {

    override fun iterator(): Iterator<T> {
        return ExceptionCatchingIterator(handler, origin.iterator())
    }

    private class ExceptionCatchingIterator<T>(
        private val handler: (Exception) -> Unit,
        private val origin: Iterator<T>
    ) : Iterator<T> by origin {

        @Suppress("TooGenericExceptionCaught")
        override fun hasNext(): Boolean {
            return try {
                origin.hasNext()
            } catch (e: Exception) {
                handler(e)
                false
            }
        }
    }
}
