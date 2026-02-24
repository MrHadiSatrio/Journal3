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

package com.hadisatrio.apps.kotlin.journal3.story

import com.benasher44.uuid.Uuid
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.moment.Moments

/**
 * A repository of [Story] objects that also provides unified cross-story [Moment] access.
 */
interface Stories : Iterable<Story> {

    /** A unified view of all moments across every story in this repository. */
    val moments: Moments

    /**
     * Creates and returns a new, empty [EditableStory] backed by this repository.
     */
    fun new(): EditableStory

    /**
     * Returns `true` if a story with the given [id] exists in this repository.
     */
    fun containsStory(id: Uuid): Boolean

    /**
     * Returns all stories whose id matches [id].
     */
    fun findStory(id: Uuid): Iterable<Story>

    /**
     * Returns `true` if any story in this repository contains at least one moment.
     */
    fun hasMoments(): Boolean

    /**
     * Returns `true` if a moment with the given [id] exists across all stories.
     */
    fun containsMoment(id: Uuid): Boolean

    /**
     * Returns all moments whose id matches [id] across all stories.
     */
    fun findMoment(id: Uuid): Iterable<Moment>

    /**
     * Returns the most recently timestamped moment across all stories.
     */
    fun mostRecentMoment(): Moment
}
