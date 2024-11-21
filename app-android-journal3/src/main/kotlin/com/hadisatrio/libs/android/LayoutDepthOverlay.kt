package com.hadisatrio.libs.android

import android.app.Activity
import android.view.View
import android.view.View.MeasureSpec
import android.view.View.MeasureSpec.EXACTLY
import android.view.ViewGroup

class LayoutDepthOverlay(private val target: ViewGroup) {

    @Throws(ClassCastException::class)
    constructor(view: View) : this(view as ViewGroup)

    constructor(activity: Activity) : this(activity.findViewById<ViewGroup>(android.R.id.content))

    fun apply(depth: Int) {
        NestedViewGroups(target, depth).forEach { group ->
            group.overlay.clear()
            val overlay = GroupDepthOverlayView(group.context)
            val width = group.measuredWidth
            val height = group.measuredHeight
            overlay.measure(MeasureSpec.makeMeasureSpec(width, EXACTLY), MeasureSpec.makeMeasureSpec(height, EXACTLY))
            overlay.layout(0, 0, width, height)
            overlay.render(LayoutDepth(group))
            group.overlay.add(overlay)
        }
    }
}
