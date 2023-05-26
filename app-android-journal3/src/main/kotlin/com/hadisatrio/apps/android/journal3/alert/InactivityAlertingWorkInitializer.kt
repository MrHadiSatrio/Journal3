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

package com.hadisatrio.apps.android.journal3.alert

import android.content.Context
import androidx.startup.Initializer
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkManagerInitializer
import com.hadisatrio.apps.android.journal3.journal3Application
import java.util.concurrent.TimeUnit

@Suppress("unused")
class InactivityAlertingWorkInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val workRequestBuilder = PeriodicWorkRequestBuilder<InactivityAlertingWork>(
            repeatInterval = context.journal3Application.inactivityAlertThreshold.inWholeMilliseconds,
            repeatIntervalTimeUnit = TimeUnit.MILLISECONDS
        )

        workManager.enqueueUniquePeriodicWork(
            /* uniqueWorkName = */
            InactivityAlertingWork::class.java.simpleName,
            /* existingPeriodicWorkPolicy = */
            ExistingPeriodicWorkPolicy.UPDATE,
            /* periodicWork = */
            workRequestBuilder.build()
        )
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf(WorkManagerInitializer::class.java)
    }
}
