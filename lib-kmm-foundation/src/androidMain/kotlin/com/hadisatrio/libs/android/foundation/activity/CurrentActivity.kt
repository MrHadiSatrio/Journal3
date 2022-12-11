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

package com.hadisatrio.libs.android.foundation.activity

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.concurrent.atomic.AtomicReference

class CurrentActivity(
    application: Application
) {

    private val activityRef: AtomicReference<Activity> = AtomicReference()

    init {
        application.registerActivityLifecycleCallbacks(ActivityLifecycleCallback(activityRef))
    }

    fun acquire(): Activity {
        var activity: Activity? = null
        while (activity == null) activity = activityRef.get()
        return activity
    }

    @Suppress("EmptyFunctionBlock")
    private class ActivityLifecycleCallback(
        private val activityRef: AtomicReference<Activity>
    ) : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {}

        override fun onActivityResumed(activity: Activity) {}

        override fun onActivityPostResumed(activity: Activity) {
            activityRef.set(activity)
        }

        override fun onActivityPrePaused(activity: Activity) {
            activityRef.set(null)
        }

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}
    }
}
