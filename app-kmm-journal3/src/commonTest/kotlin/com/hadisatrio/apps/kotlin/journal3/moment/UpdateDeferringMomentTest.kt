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

package com.hadisatrio.apps.kotlin.journal3.moment

import com.chrynan.uri.core.Uri
import com.chrynan.uri.core.fromString
import com.hadisatrio.apps.kotlin.journal3.datetime.LiteralTimestamp
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.story.SelfPopulatingStories
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.kotlin.geography.fake.FakePlace
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Instant
import org.junit.Before
import org.junit.Test

class UpdateDeferringMomentTest {

    private lateinit var original: EditableMoment
    private lateinit var updateDeferring: UpdateDeferringMoment

    @Before
    fun `Inits subjects`() {
        val stories = SelfPopulatingStories(noOfStories = 1, noOfMoments = 1, origin = FakeStories())
        original = stories.first().moments.first() as EditableMoment
        updateDeferring = UpdateDeferringMoment(original)
    }

    @Test
    fun `Defers updates to the original until its committed`() {
        updateDeferring.update(LiteralTimestamp(Instant.DISTANT_FUTURE))
        updateDeferring.update(TokenableString("foo"))
        updateDeferring.update(Sentiment(0.75F))
        updateDeferring.update(FakePlace())
        updateDeferring.update(listOf(Uri.fromString("https://foo.com")))

        original.timestamp.shouldNotBeEqual(updateDeferring.timestamp)
        original.description.shouldNotBeEqual(updateDeferring.description)
        original.sentiment.shouldNotBeEqual(updateDeferring.sentiment)
        original.place.shouldNotBeEqual(updateDeferring.place)
        original.attachments.shouldNotBeEqual(updateDeferring.attachments)

        updateDeferring.commit()

        original.timestamp.shouldBeEqual(updateDeferring.timestamp)
        original.description.shouldBeEqual(updateDeferring.description)
        original.sentiment.shouldBeEqual(updateDeferring.sentiment)
        original.place.shouldBeEqual(updateDeferring.place)
        original.attachments.shouldBeEqual(updateDeferring.attachments)
    }

    @Test
    fun `Reports whether an update has been made to it`() {
        UpdateDeferringMoment(original).run {
            updatesMade().shouldBeFalse()
        }
        UpdateDeferringMoment(original).run {
            update(LiteralTimestamp(Instant.DISTANT_FUTURE))
            updatesMade().shouldBeTrue()
        }
        UpdateDeferringMoment(original).run {
            update(TokenableString("foo"))
            updatesMade().shouldBeTrue()
        }
        UpdateDeferringMoment(original).run {
            update(Sentiment(0.75F))
            updatesMade().shouldBeTrue()
        }
        UpdateDeferringMoment(original).run {
            update(FakePlace())
            updatesMade().shouldBeTrue()
        }
        UpdateDeferringMoment(original).run {
            update(listOf(Uri.fromString("https://foo.com")))
            updatesMade().shouldBeTrue()
        }
    }

    @Test
    fun `Compares based on edited timestamp, even prior to committing`() {
        updateDeferring.update(LiteralTimestamp(Instant.DISTANT_FUTURE))
        updateDeferring.compareTo(original).shouldBe(1)
    }
}
