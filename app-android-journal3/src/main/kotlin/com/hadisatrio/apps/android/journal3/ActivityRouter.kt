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
import com.benasher44.uuid.Uuid
import com.hadisatrio.apps.android.journal3.story.EditAStoryActivity
import com.hadisatrio.apps.kotlin.journal3.Router

class ActivityRouter(
    private val activity: Activity
) : Router {

    override fun toStoryEditor() {
        activity.runOnUiThread {
            val intent = Intent(activity, EditAStoryActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun toMomentEditor() {
        TODO("Not yet implemented")
    }

    override fun toStoryEditor(id: Uuid) {
        activity.runOnUiThread {
            val intent = Intent(activity, EditAStoryActivity::class.java)
            intent.putExtra("target_id", id.toString())
            activity.startActivity(intent)
        }
    }

    override fun toStoryDetail(id: Uuid) {
        TODO("Not yet implemented")
    }

    override fun toMomentDetail(id: Uuid) {
        TODO("Not yet implemented")
    }

    override fun toPrevious() {
        activity.runOnUiThread {
            activity.finish()
        }
    }
}
