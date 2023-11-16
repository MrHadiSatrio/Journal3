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

import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import kotlin.test.BeforeTest
import kotlin.test.Test

class UpdateDeferringStoryTest {

    private lateinit var original: EditableStory
    private lateinit var updateDeferring: UpdateDeferringStory

    @BeforeTest
    fun `Inits subjects`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 1, origin = FakeStories())
        original = stories.first() as EditableStory
        updateDeferring = UpdateDeferringStory(original)
    }

    @Test
    fun `Defers updates to the original until its committed`() {
        updateDeferring.update("foo")
        updateDeferring.update(TokenableString("foo"))

        original.title.shouldNotBeEqual(updateDeferring.title)
        original.synopsis.shouldNotBeEqual(updateDeferring.synopsis)

        updateDeferring.commit()

        original.title.shouldBeEqual(updateDeferring.title)
        original.synopsis.shouldBeEqual(updateDeferring.synopsis)
    }

    @Test
    fun `Reports whether an update has been made to it`() {
        UpdateDeferringStory(original).run {
            updatesMade().shouldBeFalse()
        }
        UpdateDeferringStory(original).run {
            update("foo")
            updatesMade().shouldBeTrue()
        }
        UpdateDeferringStory(original).run {
            update(TokenableString("foo"))
            updatesMade().shouldBeTrue()
        }
    }
}
