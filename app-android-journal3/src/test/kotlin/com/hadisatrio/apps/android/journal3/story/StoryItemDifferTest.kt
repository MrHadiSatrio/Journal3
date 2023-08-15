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

package com.hadisatrio.apps.android.journal3.story

import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStory
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import org.junit.Test

class StoryItemDifferTest {

    @Test
    fun `Reports two stories with differing IDs as being different`() {
        val one = FakeStory(uuid4(), mutableListOf())
        val another = FakeStory(uuid4(), mutableListOf())

        StoryItemDiffer.areItemsTheSame(one, another).shouldBeFalse()
    }

    @Test
    fun `Reports two stories with the same ID as being same`() {
        val id = uuid4()
        val one = FakeStory(id, mutableListOf())
        val another = FakeStory(id, mutableListOf())

        StoryItemDiffer.areItemsTheSame(one, another).shouldBeTrue()
    }

    @Test
    fun `Reports two stories with identical content as being same content-wise`() {
        val id = uuid4()
        val one = FakeStory(id, mutableListOf())
        val another = FakeStory(id, mutableListOf())

        StoryItemDiffer.areContentsTheSame(one, another).shouldBeTrue()
    }

    @Test
    fun `Reports two stories with differing title as being different content-wise`() {
        val id = uuid4()
        val one = FakeStory(id, mutableListOf())
        val another = FakeStory(id, mutableListOf())
        one.update("Foo")
        another.update("Bar")

        StoryItemDiffer.areContentsTheSame(one, another).shouldBeFalse()
    }

    @Test
    fun `Reports two stories with differing synopsis as being different content-wise`() {
        val id = uuid4()
        val one = FakeStory(id, mutableListOf())
        val another = FakeStory(id, mutableListOf())
        one.update(TokenableString("Foo"))
        another.update(TokenableString("Bar"))

        StoryItemDiffer.areContentsTheSame(one, another).shouldBeFalse()
    }

    @Test
    fun `Reports two stories with differing moments as being different content-wise`() {
        val id = uuid4()
        val one = FakeStory(id, mutableListOf())
        val another = FakeStory(id, mutableListOf())
        one.new().update(TokenableString("Foo"))
        another.new().update(TokenableString("Bar"))

        StoryItemDiffer.areContentsTheSame(one, another).shouldBeFalse()
    }
}
