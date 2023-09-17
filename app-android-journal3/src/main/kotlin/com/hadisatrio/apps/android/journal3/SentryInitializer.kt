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

package com.hadisatrio.apps.android.journal3

import android.content.Context
import androidx.startup.Initializer
import io.sentry.android.core.SentryAndroid

@Suppress("unused")
class SentryInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        SentryAndroid.init(context) { options ->
            options.dsn = BuildConfig.KEY_SENTRY
            options.environment = BuildConfig.BUILD_TYPE
            options.isDebug = BuildConfig.DEBUG

            val perfSamplingRate = if (BuildConfig.DEBUG) PERF_SAMPLING_RATE_FULL else PERF_SAMPLING_RATE_PARTIAL
            options.tracesSampleRate = perfSamplingRate
            options.profilesSampleRate = perfSamplingRate

            options.isAnrReportInDebug = true
            options.isAttachAnrThreadDump = true
            options.isReportHistoricalAnrs = true
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    companion object {

        private const val PERF_SAMPLING_RATE_FULL = 1.0
        private const val PERF_SAMPLING_RATE_PARTIAL = 0.25
    }
}
