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

package com.hadisatrio.apps.kotlin.journal3.story.cache

import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CachingStoriesPresenter(
    private val scope: CoroutineScope,
    private val origin: Presenter<Stories>
) : Presenter<Stories> {

    override fun present(thing: Stories) {
        scope.launch {
            origin.present(CachingStories(thing))
        }
    }
}
