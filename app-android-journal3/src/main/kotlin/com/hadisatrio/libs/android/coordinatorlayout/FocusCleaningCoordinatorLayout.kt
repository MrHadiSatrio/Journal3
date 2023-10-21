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

package com.hadisatrio.libs.android.coordinatorlayout

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.hadisatrio.apps.android.journal3.journal3Application

class FocusCleaningCoordinatorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CoordinatorLayout(context, attrs) {

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        journal3Application.backgroundExecutor.execute {
            if (event.action != MotionEvent.ACTION_DOWN) return@execute
            val focusedView = findFocus() ?: return@execute
            if (event.happenedWithin(focusedView.visibleBounds)) return@execute
            journal3Application.foregroundExecutor.execute { focusedView.clearFocus() }
            hideSoftInput(focusedView)
        }
        return super.dispatchTouchEvent(event)
    }

    private fun hideSoftInput(view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun MotionEvent.happenedWithin(bounds: Rect): Boolean {
        return bounds.contains(rawX.toInt(), rawY.toInt())
    }

    private val View.visibleBounds: Rect get() {
        return Rect().apply { getGlobalVisibleRect(this) }
    }
}
