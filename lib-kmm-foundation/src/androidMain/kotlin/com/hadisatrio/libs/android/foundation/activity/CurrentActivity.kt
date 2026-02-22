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

/**
 * A reference to the currently resumed [Activity] of the given [Application].
 *
 * Keeps track on basis of [Application.ActivityLifecycleCallbacks]; updating
 * a queryable internal reference whenever a new [Activity] completes its
 * `onResume()` callback.
 *
 * @param application Application whose activity lifecycle is observed.
 */
class CurrentActivity(
    application: Application
) {

    private val activityRef: AtomicReference<Activity> = AtomicReference()

    init {
        application.registerActivityLifecycleCallbacks(ActivityLifecycleCallback(activityRef))
    }

    /**
     * Returns the currently resumed [Activity], blocking the calling thread until one is available.
     *
     * @return The currently resumed [Activity].
     */
    fun acquire(): Activity {
        var activity: Activity? = null
        while (activity == null) activity = activityRef.get()
        return activity
    }

    private class ActivityLifecycleCallback(
        private val activityRef: AtomicReference<Activity>
    ) : Application.ActivityLifecycleCallbacks {

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            // Nothing to do.
        }

        override fun onActivityStarted(activity: Activity) {
            // Nothing to do.
        }

        override fun onActivityResumed(activity: Activity) {
            // Nothing to do.
        }

        override fun onActivityPostResumed(activity: Activity) {
            activityRef.set(activity)
        }

        override fun onActivityPrePaused(activity: Activity) {
            activityRef.set(null)
        }

        override fun onActivityPaused(activity: Activity) {
            // Nothing to do.
        }

        override fun onActivityStopped(activity: Activity) {
            // Nothing to do.
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            // Nothing to do.
        }

        override fun onActivityDestroyed(activity: Activity) {
            // Nothing to do.
        }
    }
}
