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

package com.hadisatrio.libs.android.foundation

import com.hadisatrio.libs.kotlin.foundation.UseCase
import java.util.concurrent.Executor

/**
 * A [UseCase] that dispatches the invocation of another [UseCase] to an [Executor].
 *
 * @param executor Executor that runs [origin].
 * @param origin The [UseCase] to invoke on [executor].
 */
class ExecutorDispatchingUseCase(
    private val executor: Executor,
    private val origin: UseCase
) : UseCase {

    override fun invoke() {
        executor.execute { origin() }
    }
}
