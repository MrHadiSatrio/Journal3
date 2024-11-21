package com.hadisatrio.libs.android

import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isGone

class NestedViewGroups(
    private val root: ViewGroup,
    private val targetDepth: Int
) : Sequence<ViewGroup> {

    init {
        require(targetDepth >= 0) {
            "It's impossible to look for nested view groups with a negative target depth."
        }
    }

    override fun iterator(): Iterator<ViewGroup> {
        return root.childrenAtDepth(targetDepth).iterator()
    }

    private fun ViewGroup.childrenAtDepth(depth: Int): Sequence<ViewGroup> {
        var c = emptySequence<ViewGroup>()
        if (depth == 0) {
            c += this
        } else {
            val nestedGroups = children.filterIsInstance<ViewGroup>().filterNot { it.isGone }
            c += nestedGroups.flatMap { it.childrenAtDepth(depth - 1) }
        }
        return c
    }
}