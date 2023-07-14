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

import android.app.Activity
import android.content.Intent
import com.hadisatrio.apps.android.journal3.geography.SelectAPlaceActivity
import com.hadisatrio.apps.android.journal3.moment.DeleteAMomentActivity
import com.hadisatrio.apps.android.journal3.moment.EditAMomentActivity
import com.hadisatrio.apps.android.journal3.story.DeleteAStoryActivity
import com.hadisatrio.apps.android.journal3.story.EditAStoryActivity
import com.hadisatrio.apps.android.journal3.story.ViewStoryActivity
import com.hadisatrio.libs.android.foundation.activity.CurrentActivity
import com.hadisatrio.libs.kotlin.foundation.event.Event
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent

class ActivityRoutingEventSink(
    private val currentActivity: CurrentActivity
) : EventSink {

    override fun sink(event: Event) {
        if (event !is SelectionEvent || event.selectionKind != "action") return
        val identifier = event.selectedIdentifier
        val activity = currentActivity.acquire()
        when (identifier) {
            "add_story" -> activity.startActivity(Intent(activity, EditAStoryActivity::class.java))
            "edit_story" -> activity.startEditAStoryActivity(event)
            "delete_story" -> activity.startDeleteAStoryActivity(event)
            "view_story" -> activity.startViewStoryActivity(event)
            "view_stories" -> activity.startViewStoriesActivity()
            "add_moment" -> activity.startAddAMomentActivity(event)
            "edit_moment" -> activity.startEditAMomentActivity(event)
            "delete_moment" -> activity.startDeleteAMomentActivity(event)
            "select_place" -> activity.startActivity(Intent(activity, SelectAPlaceActivity::class.java))
        }
    }

    private fun Activity.startViewStoriesActivity() {
        val intent = Intent(this, RootActivity::class.java)
        intent.setAction("view_stories")
        startActivity(intent)
    }

    private fun Activity.startViewStoryActivity(event: SelectionEvent) {
        val intent = Intent(this, ViewStoryActivity::class.java)
        intent.putExtra("target_id", event["story_id"])
        startActivity(intent)
    }

    private fun Activity.startEditAStoryActivity(event: SelectionEvent) {
        val intent = Intent(this, EditAStoryActivity::class.java)
        intent.putExtra("target_id", event["story_id"])
        startActivity(intent)
    }

    private fun Activity.startDeleteAStoryActivity(event: SelectionEvent) {
        val intent = Intent(this, DeleteAStoryActivity::class.java)
        intent.putExtra("target_id", event["story_id"])
        startActivity(intent)
    }

    private fun Activity.startAddAMomentActivity(event: SelectionEvent) {
        val intent = Intent(this, EditAMomentActivity::class.java)
        intent.putExtra("story_id", event["story_id"])
        startActivity(intent)
    }

    private fun Activity.startEditAMomentActivity(event: SelectionEvent) {
        val intent = Intent(this, EditAMomentActivity::class.java)
        intent.putExtra("target_id", event["moment_id"])
        intent.putExtra("story_id", event["story_id"])
        startActivity(intent)
    }

    private fun Activity.startDeleteAMomentActivity(event: SelectionEvent) {
        val intent = Intent(this, DeleteAMomentActivity::class.java)
        intent.putExtra("target_id", event["moment_id"])
        startActivity(intent)
    }
}
