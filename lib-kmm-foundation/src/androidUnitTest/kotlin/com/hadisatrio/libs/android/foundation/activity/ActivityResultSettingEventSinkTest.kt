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
import android.os.Parcel
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.test.runner.AndroidJUnit4
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Adapter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.Shadows
import java.io.Serializable

@RunWith(AndroidJUnit4::class)
class ActivityResultSettingEventSinkTest {

    private val activityController = Robolectric.buildActivity(ComponentActivity::class.java)
    private val activity = activityController.get()
    private val shadowActivity = Shadows.shadowOf(activity)
    private val adapter = Adapter<Event, Map<String, Any>> { event ->
        if (event is SelectionEvent) {
            mapOf(
                "str" to "foobar",
                "int" to 42,
                "bol" to true,
                "flt" to 3.14f,
                "dbl" to 3.14,
                "lng" to 42L,
                "prc" to FakeParcelable("foobar"),
                "srz" to FakeSerializable("foobar")
            )
        } else {
            emptyMap()
        }
    }
    private val eventSink = ActivityResultSettingEventSink(activity, adapter)

    @Before
    fun `Starts activity`() {
        activityController.setup().visible()
    }

    @Test
    fun `Sets RESULT_OK as the activity result upon receiving a non-empty value set`() {
        eventSink.sink(SelectionEvent("foo", "bar"))

        shadowActivity.resultCode.shouldBe(Activity.RESULT_OK)
        shadowActivity.resultIntent.extras?.let { bundle ->
            bundle.getString("str").shouldBe("foobar")
            bundle.getInt("int").shouldBe(42)
            bundle.getBoolean("bol").shouldBe(true)
            bundle.getFloat("flt").shouldBe(3.14f)
            bundle.getDouble("dbl").shouldBe(3.14)
            bundle.getLong("lng").shouldBe(42L)
            bundle.getParcelable<FakeParcelable>("prc").let { prc ->
                prc.shouldBeInstanceOf<FakeParcelable>()
                prc.value.shouldBe("foobar")
            }
            bundle.getSerializable("srz")!!.let { srz ->
                srz.shouldBeInstanceOf<FakeSerializable>()
                srz.value.shouldBe("foobar")
            }
        }
    }

    @Test
    fun `Sets RESULT_CANCELED as the activity result upon receiving an empty value set`() {
        eventSink.sink(CompletionEvent())

        shadowActivity.resultCode.shouldBe(Activity.RESULT_CANCELED)
        shadowActivity.resultIntent.shouldBeNull()
    }

    @Test
    fun `Throws upon receiving a non-empty value set with unknown type`() {
        val badAdapter = Adapter<Event, Map<String, Any>> { event ->
            if (event is SelectionEvent) {
                mapOf("foo" to FakeNonSerializableNorParcelable())
            } else {
                emptyMap()
            }
        }
        val eventSink = ActivityResultSettingEventSink(activity, badAdapter)

        shouldThrow<IllegalArgumentException> {
            eventSink.sink(SelectionEvent("foo", "bar"))
        }
    }

    private class FakeParcelable(val value: String) : Parcelable {

        constructor(parcel: Parcel) : this(parcel.readString()!!)

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(value)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<FakeParcelable> {

            override fun createFromParcel(parcel: Parcel): FakeParcelable {
                return FakeParcelable(parcel)
            }

            override fun newArray(size: Int): Array<FakeParcelable?> {
                return arrayOfNulls(size)
            }
        }
    }

    @Suppress("SerialVersionUIDInSerializableClass")
    private class FakeSerializable(val value: String) : Serializable

    private class FakeNonSerializableNorParcelable
}
