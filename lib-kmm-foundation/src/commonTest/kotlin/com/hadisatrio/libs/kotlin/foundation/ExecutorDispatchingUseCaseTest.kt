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

package com.hadisatrio.libs.kotlin.foundation

import com.hadisatrio.libs.kotlin.foundation.concurrent.CurrentThreadExecutor
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.util.concurrent.Executor
import kotlin.test.Test

class ExecutorDispatchingUseCaseTest {

    private val executor: Executor = spyk(CurrentThreadExecutor())
    private val origin: UseCase = mockk(relaxUnitFun = true)
    private val useCase = ExecutorDispatchingUseCase(executor, origin)

    @Test
    fun `Forwards call to the origin on the given executor`() {
        useCase()
        verify(exactly = 1) { executor.execute(any()) }
        verify(exactly = 1) { origin.invoke() }
    }
}
