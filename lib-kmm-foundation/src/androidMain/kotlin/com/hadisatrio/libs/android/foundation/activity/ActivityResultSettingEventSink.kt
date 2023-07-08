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

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.presentation.Adapter
import java.io.Serializable

class ActivityResultSettingEventSink(
    private val activity: Activity,
    private val adapter: Adapter<Event, Map<String, Any>>
) : EventSink {

    override fun sink(event: Event) {
        val values = adapter.adapt(event)
        val intent = Intent().apply { putExtras(values.toBundle()) }
        if (values.isEmpty()) return
        activity.setResult(Activity.RESULT_OK, intent)
    }

    // Lint raises false-positive warning about the usage of forEach() within this function.
    // This is a recurring bug from Lint wherein it fails to read the metadata of newer Kotlin
    // binaries. This is possibly addressed in newer versions of Lint, but since it means we'd
    // have to upgrade the entire Android Gradle Plugin, we'll just suppress the warning for now.
    // See https://issuetracker.google.com/issues/175334545 for more details.
    @SuppressLint("NewApi")
    private fun Map<String, Any>.toBundle(): Bundle {
        val bundle = Bundle()
        forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                is Float -> bundle.putFloat(key, value)
                is Double -> bundle.putDouble(key, value)
                is Parcelable -> bundle.putParcelable(key, value)
                is Serializable -> bundle.putSerializable(key, value)
                else -> throw IllegalArgumentException("Unsupported type of '${value::class.java}'.")
            }
        }
        return bundle
    }
}
