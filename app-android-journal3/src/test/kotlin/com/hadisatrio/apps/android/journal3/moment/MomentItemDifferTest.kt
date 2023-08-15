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

package com.hadisatrio.apps.android.journal3.moment

import com.benasher44.uuid.uuid4
import com.chrynan.uri.core.Uri
import com.chrynan.uri.core.fromString
import com.hadisatrio.apps.kotlin.journal3.datetime.LiteralTimestamp
import com.hadisatrio.apps.kotlin.journal3.moment.fake.FakeMoment
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.kotlin.geography.fake.FakePlace
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import kotlinx.datetime.Instant
import org.junit.Test

class MomentItemDifferTest {
    @Test
    fun `Reports two moments with differing IDs as being different`() {
        val one = FakeMoment(uuid4(), mutableListOf())
        val another = FakeMoment(uuid4(), mutableListOf())

        MomentItemDiffer.areItemsTheSame(one, another).shouldBeFalse()
    }

    @Test
    fun `Reports two moments with the same ID as being same`() {
        val id = uuid4()
        val one = FakeMoment(id, mutableListOf())
        val another = FakeMoment(id, mutableListOf())

        MomentItemDiffer.areItemsTheSame(one, another).shouldBeTrue()
    }

    @Test
    fun `Reports two moments with identical content as being same content-wise`() {
        val id = uuid4()
        val one = FakeMoment(id, mutableListOf())
        val another = FakeMoment(id, mutableListOf())

        MomentItemDiffer.areContentsTheSame(one, another).shouldBeTrue()
    }

    @Test
    fun `Reports two moments with differing timestamp as being different content-wise`() {
        val id = uuid4()
        val one = FakeMoment(id, mutableListOf())
        val another = FakeMoment(id, mutableListOf())
        one.update(LiteralTimestamp(Instant.DISTANT_FUTURE))
        another.update(LiteralTimestamp(Instant.DISTANT_PAST))

        MomentItemDiffer.areContentsTheSame(one, another).shouldBeFalse()
    }

    @Test
    fun `Reports two moments with differing description as being different content-wise`() {
        val id = uuid4()
        val one = FakeMoment(id, mutableListOf())
        val another = FakeMoment(id, mutableListOf())
        one.update(TokenableString("Foo"))
        another.update(TokenableString("Bar"))

        MomentItemDiffer.areContentsTheSame(one, another).shouldBeFalse()
    }

    @Test
    fun `Reports two moments with differing sentiment as being different content-wise`() {
        val id = uuid4()
        val one = FakeMoment(id, mutableListOf())
        val another = FakeMoment(id, mutableListOf())
        one.update(Sentiment(1.0F))
        another.update(Sentiment(0.0F))

        MomentItemDiffer.areContentsTheSame(one, another).shouldBeFalse()
    }

    @Test
    fun `Reports two moments with differing places as being different content-wise`() {
        val id = uuid4()
        val one = FakeMoment(id, mutableListOf())
        val another = FakeMoment(id, mutableListOf())
        one.update(FakePlace())
        another.update(FakePlace())

        MomentItemDiffer.areContentsTheSame(one, another).shouldBeFalse()
    }

    @Test
    fun `Reports two moments with differing attachments as being different content-wise`() {
        val id = uuid4()
        val one = FakeMoment(id, mutableListOf())
        val another = FakeMoment(id, mutableListOf())
        one.update(listOf(Uri.fromString("https://foo.bar")))
        another.update(listOf(Uri.fromString("https://fizz.buzz")))

        MomentItemDiffer.areContentsTheSame(one, another).shouldBeFalse()
    }
}
