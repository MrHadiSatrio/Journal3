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

package com.hadisatrio.apps.android.journal3.moment

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.startup.Initializer
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkManagerInitializer
import java.util.concurrent.TimeUnit

@Suppress("unused")
class MomentCapturingWorkInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val workManager = WorkManager.getInstance(context)

        if (!hasRequiredPermissions(context)) {
            workManager.cancelUniqueWork(WORK_ID)
            return
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        val workRequestBuilder = PeriodicWorkRequestBuilder<MomentCapturingWork>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES,
            flexTimeInterval = 5,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
        workManager.enqueueUniquePeriodicWork(
            /* uniqueWorkName = */
            WORK_ID,
            /* existingPeriodicWorkPolicy = */
            ExistingPeriodicWorkPolicy.KEEP,
            /* periodicWork = */
            workRequestBuilder.setConstraints(constraints).build()
        )
    }

    private fun hasRequiredPermissions(context: Context): Boolean {
        var permissions = 0
        permissions += context.checkSelfPermission(ACCESS_COARSE_LOCATION)
        permissions += context.checkSelfPermission(ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions += context.checkSelfPermission(ACCESS_BACKGROUND_LOCATION)
        }
        return permissions == PackageManager.PERMISSION_GRANTED
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf(WorkManagerInitializer::class.java)
    }

    private companion object {
        const val WORK_ID = "91073087-9cae-4c08-818d-45537d222390"
    }
}
