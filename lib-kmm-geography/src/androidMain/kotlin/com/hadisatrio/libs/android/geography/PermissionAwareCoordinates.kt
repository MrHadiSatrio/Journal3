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

package com.hadisatrio.libs.android.geography

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.hadisatrio.libs.android.foundation.activity.CurrentActivity
import com.hadisatrio.libs.kotlin.geography.Coordinates
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class PermissionAwareCoordinates(
    private val currentActivity: CurrentActivity,
    private val origin: Coordinates
) : Coordinates() {

    override val latitude: Double get() {
        return if (checkPermission()) {
            origin.latitude
        } else {
            throw SecurityException("Required permission(s) is not granted.")
        }
    }
    override val longitude: Double get() {
        return if (checkPermission()) {
            origin.longitude
        } else {
            throw SecurityException("Required permission(s) is not granted.")
        }
    }

    private fun checkPermission(): Boolean {
        val activity = currentActivity.acquire()
        val blockingQueue = LinkedBlockingQueue<Boolean>()

        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            return true
        }

        activity.runOnUiThread {
            activity.askForPermissions(Permission.ACCESS_FINE_LOCATION) { result ->
                blockingQueue.offer(result.isAllGranted(Permission.ACCESS_FINE_LOCATION))
            }
        }
        return blockingQueue.poll(Long.MAX_VALUE, TimeUnit.MILLISECONDS)
    }
}
