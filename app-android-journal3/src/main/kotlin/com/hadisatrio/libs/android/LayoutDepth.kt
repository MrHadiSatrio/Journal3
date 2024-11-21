package com.hadisatrio.libs.android

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isGone
import kotlin.Throws

class LayoutDepth(private val root: ViewGroup) : Number() {

    @Throws(ClassCastException::class)
    constructor(view: View): this(view as ViewGroup)

    constructor(activity: Activity) : this(activity.findViewById<ViewGroup>(android.R.id.content))

    fun toColor(): Int {
        return when(toInt()) {
            in 0..5 -> Color.GREEN
            in 6..10 -> Color.YELLOW
            else -> Color.RED
        }
    }

    override fun toInt(): Int {
        if (root.childCount < 1) return 0
        var depth = 1
        val nestedGroups = root.children.filterIsInstance<ViewGroup>().filterNot { it.isGone }
        nestedGroups.forEach { group -> depth = maxOf(depth, LayoutDepth(group).toInt() + 1) }
        return depth
    }

    override fun toByte(): Byte {
        return toInt().toByte()
    }

    override fun toDouble(): Double {
        return toInt().toDouble()
    }

    override fun toFloat(): Float {
        return toInt().toFloat()
    }

    override fun toLong(): Long {
        return toInt().toLong()
    }

    override fun toShort(): Short {
        return toInt().toShort()
    }

    override fun toString(): String {
        return toInt().toString()
    }
}
