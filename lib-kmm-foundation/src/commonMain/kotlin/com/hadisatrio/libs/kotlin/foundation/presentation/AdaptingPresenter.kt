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

/**
 * A [Presenter] that converts its input through an [Adapter] before delegating to another [Presenter].
 *
 * @param origin The delegate presenter that receives the converted value.
 * @param adapter Converts the incoming value from [I] to [O].
 */
class AdaptingPresenter<I, O>(
    private val origin: Presenter<O>,
    private val adapter: Adapter<I, O>
) : Presenter<I> {

    override fun present(thing: I) {
        origin.present(adapter.adapt(thing))
    }
}
