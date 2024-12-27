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
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.kotlin.journal3.moment.CaptureAMomentUseCase

class MomentCapturingWork(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        if (!hasRequiredPermissions(applicationContext)) {
            return Result.failure()
        }
        CaptureAMomentUseCase(
            story = journal3Application.story,
            places = journal3Application.places,
            speed = journal3Application.speed,
            coordinates = journal3Application.coordinates,
            clock = journal3Application.clock
        )()
        return Result.success()
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
}
