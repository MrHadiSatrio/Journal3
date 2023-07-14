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

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationBarView
import com.hadisatrio.apps.android.journal3.story.StoriesListFragment
import com.hadisatrio.libs.android.foundation.lifecycle.LifecycleTriggeredEventSource
import com.hadisatrio.libs.android.foundation.material.NavigationBarSelectionEventSource
import com.hadisatrio.libs.android.foundation.widget.ViewClickEventSource
import com.hadisatrio.libs.android.viewpager2.SimpleFragmentPagerAdapter
import com.hadisatrio.libs.android.viewpager2.SimpleFragmentPagerAdapter.FragmentFactory
import com.hadisatrio.libs.kotlin.foundation.ExecutorDispatchingUseCase
import com.hadisatrio.libs.kotlin.foundation.UseCase
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.EventSource
import com.hadisatrio.libs.kotlin.foundation.event.EventSources
import com.hadisatrio.libs.kotlin.foundation.event.ExecutorDispatchingEventSource
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.event.StreamEventsUseCase

class RootActivity : AppCompatActivity() {

    private val pager: ViewPager2 by lazy {
        findViewById(R.id.pager)
    }

    private val bottomBar: NavigationBarView by lazy {
        findViewById(R.id.bottom_bar)
    }

    private val eventSource: EventSource by lazy {
        ExecutorDispatchingEventSource(
            executor = journal3Application.foregroundExecutor,
            origin = EventSources(
                journal3Application.globalEventSource,
                LifecycleTriggeredEventSource(
                    lifecycleOwner = this,
                    lifecycleEvent = Lifecycle.Event.ON_DESTROY,
                    eventFactory = { CancellationEvent("system") }
                ),
                ViewClickEventSource(
                    view = findViewById(R.id.add_button),
                    eventFactory = { SelectionEvent("action", "add_story") }
                ),
                NavigationBarSelectionEventSource(
                    view = bottomBar,
                    eventFactory = { SelectionEvent("action", "view_stories") }
                )
            )
        )
    }

    private val useCase: UseCase by lazy {
        ExecutorDispatchingUseCase(
            executor = journal3Application.backgroundExecutor,
            origin = StreamEventsUseCase(
                eventSource = eventSource,
                eventSink = journal3Application.globalEventSink
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
        useCase()
    }

    private fun setupViews() {
        setContentView(R.layout.activity_root)
        pager.isUserInputEnabled = false
        pager.adapter = SimpleFragmentPagerAdapter(this, FragmentFactory { StoriesListFragment() })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val (menuId, pagerItem) = when (intent?.action) {
            "view_stories" -> R.id.view_stories_menu_item to 0
            else -> return
        }
        if (bottomBar.selectedItemId != menuId) bottomBar.selectedItemId = menuId
        if (pager.currentItem != pagerItem) pager.currentItem = pagerItem
    }
}
