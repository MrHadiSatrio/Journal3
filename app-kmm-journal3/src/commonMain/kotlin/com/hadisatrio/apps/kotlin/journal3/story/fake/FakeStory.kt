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

package com.hadisatrio.apps.kotlin.journal3.story.fake

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.moment.Moments
import com.hadisatrio.apps.kotlin.journal3.moment.fake.FakeMoment
import com.hadisatrio.apps.kotlin.journal3.moment.fake.FakeMoments
import com.hadisatrio.apps.kotlin.journal3.story.Story
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString

class FakeStory(
    override val id: Uuid,
    private val group: MutableList<Story>
) : Story {

    private var isForgotten: Boolean = false
    private val rawMoments: MutableList<Moment> = mutableListOf()

    override var title: String = ""
        private set
    override var synopsis: TokenableString = TokenableString.EMPTY
        private set
    override var moments: Moments = FakeMoments(rawMoments)
        private set

    override fun update(title: String) {
        require(!isForgotten) { "This story has already been forgotten." }
        this.title = title
    }

    override fun update(synopsis: TokenableString) {
        require(!isForgotten) { "This story has already been forgotten." }
        this.synopsis = synopsis
    }

    override fun new(): Moment {
        val moment = FakeMoment(uuid4(), rawMoments)
        rawMoments.add(moment)
        return moment
    }

    override fun forget() {
        isForgotten = true
        group.remove(this)
    }

    override fun compareTo(other: Story): Int {
        return title.compareTo(other.title)
    }
}
