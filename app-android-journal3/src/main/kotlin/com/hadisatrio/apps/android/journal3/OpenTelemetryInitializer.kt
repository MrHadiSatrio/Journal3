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

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import io.opentelemetry.android.OpenTelemetryRum
import io.opentelemetry.android.config.OtelRumConfig
import io.opentelemetry.android.features.diskbuffering.DiskBufferingConfiguration
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter

@Suppress("unused")
class OpenTelemetryInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        val diskBufferingConfig =
            DiskBufferingConfiguration.builder()
                .setEnabled(true)
                .setMaxCacheSize(MAX_CACHE_BYTES)
                .build()
        val config =
            OtelRumConfig()
                .setDiskBufferingConfiguration(diskBufferingConfig)

        val otelAuthority = BuildConfig.OPENTELEMETRY_AUTHORITY
        val spansIngestUrl = "http://$otelAuthority/${BuildConfig.OPENTELEMETRY_TRACES_PATH}"
        val logsIngestUrl = "http://$otelAuthority/${BuildConfig.OPENTELEMETRY_LOGS_PATH}"
        OpenTelemetryRum.builder(context.applicationContext as Application, config)
            .addSpanExporterCustomizer {
                OtlpHttpSpanExporter.builder()
                    .setEndpoint(spansIngestUrl)
                    .build()
            }
            .addLogRecordExporterCustomizer {
                OtlpHttpLogRecordExporter.builder()
                    .setEndpoint(logsIngestUrl)
                    .build()
            }
            .build()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }

    companion object {
        private const val MAX_CACHE_BYTES = 10_000_000
    }
}
