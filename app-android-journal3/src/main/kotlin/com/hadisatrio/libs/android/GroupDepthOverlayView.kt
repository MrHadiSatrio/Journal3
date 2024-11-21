package com.hadisatrio.libs.android

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.hadisatrio.apps.android.journal3.R

class GroupDepthOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.view_group_depth_overlay, this)
    }

    fun render(depth: LayoutDepth) {
        findViewById<View>(R.id.tint_overlay).setBackgroundColor(depth.toColor())
        findViewById<TextView>(R.id.depth_label).text = depth.toString()
    }
}
