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

@file:OptIn(ExperimentalTime::class)

package com.hadisatrio.apps.android.journal3.datetime

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.libs.android.fragment.supportFragmentManager
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

class TimestampSelectorButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialButton(context, attrs),
    View.OnClickListener {

    private var selection: Timestamp = Timestamp.DEFAULT
    private var activePicker: DialogFragment? = null
    private var listener: OnTimestampSelectedListener? = null

    fun setOnTimestampSelectedListener(listener: OnTimestampSelectedListener?) {
        this.listener = listener
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        applySelection(selection)
        super.setOnClickListener(this)
    }

    override fun onDetachedFromWindow() {
        super.setOnClickListener(null)
        super.onDetachedFromWindow()
    }

    override fun setOnClickListener(l: OnClickListener?) {
        throw UnsupportedOperationException("Setting an external click listener is forbidden.")
    }

    override fun onClick(p0: View?) {
        if (activePicker != null) return

        val fragmentManager = this.supportFragmentManager ?: return
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(this.selection.toEpochMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener {
            applySelection(it)

            val dateSelection = Instant.fromEpochMilliseconds(it)
            val timePicker = MaterialTimePicker.Builder()
                .setHour(dateSelection.toLocalDateTime(TimeZone.currentSystemDefault()).hour)
                .setMinute(dateSelection.toLocalDateTime(TimeZone.currentSystemDefault()).minute)
                .build()

            timePicker.addOnPositiveButtonClickListener {
                val dateTimeSelection = dateSelection.plus(timePicker.hour.hours)
                    .plus(timePicker.minute.minutes)

                applySelection(dateTimeSelection)
            }
            timePicker.show(fragmentManager, FRAGMENT_TAG_PICKER)
            activePicker = timePicker
        }

        datePicker.show(fragmentManager, FRAGMENT_TAG_PICKER)
        activePicker = datePicker
    }

    override fun onSaveInstanceState(): Parcelable {
        return bundleOf(
            STATE_KEY_SUPER to super.onSaveInstanceState(),
            STATE_SELECTION to (this.selection.toEpochMilliseconds()),
            STATE_HAS_ACTIVE_PICKER to (this.activePicker != null)
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable(STATE_KEY_SUPER))
            state.getLong(STATE_SELECTION).let(::applySelection)

            if (state.getBoolean(STATE_HAS_ACTIVE_PICKER)) {
                val fragmentManager = this.supportFragmentManager ?: return
                val fragmentTransaction = fragmentManager.beginTransaction()
                val picker = fragmentManager.findFragmentByTag(FRAGMENT_TAG_PICKER)
                if (picker != null) fragmentTransaction.remove(picker)
                fragmentTransaction.commitNow()
            }
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private fun applySelection(epochMillis: Long) {
        applySelection(Instant.fromEpochMilliseconds(epochMillis))
    }

    private fun applySelection(instant: Instant) {
        applySelection(Timestamp(instant))
    }

    internal fun applySelection(timestamp: Timestamp) {
        selection = timestamp
        text = timestamp.toString()
        listener?.onTimeStampSelected(timestamp)
    }

    interface OnTimestampSelectedListener {
        fun onTimeStampSelected(timestamp: Timestamp)
    }

    companion object {
        private const val STATE_KEY_SUPER = "super_state"
        private const val STATE_SELECTION = "selection"
        private const val STATE_HAS_ACTIVE_PICKER = "has_active_picker"

        private val FRAGMENT_TAG_PICKER = "${TimestampSelectorButton::class.simpleName}Fragment"
    }
}
