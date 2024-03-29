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

import com.hadisatrio.apps.kotlin.journal3.event.UnsupportedEvent
import com.hadisatrio.apps.kotlin.journal3.id.INVALID_UUID
import com.hadisatrio.apps.kotlin.journal3.story.SelfPopulatingStories
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.RecordedEventSource
import com.hadisatrio.libs.kotlin.foundation.event.fake.FakeEventSink
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.modal.ModalApprovalEvent
import com.hadisatrio.libs.kotlin.foundation.modal.ModalDismissalEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.fake.FakePresenter
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class DeleteMomentUseCaseTest {

    private val stories = SelfPopulatingStories(1, 1, FakeStories())
    private val story = stories.first()
    private val moment = story.moments.first()
    private val presenter = FakePresenter<Modal>()
    private val eventSink = FakeEventSink()

    @Test
    fun `Deletes the moment after the user confirms the request`() {
        DeleteMomentUseCase(
            momentId = moment.id,
            stories = stories,
            presenter = presenter,
            eventSource = RecordedEventSource(
                ModalApprovalEvent("forgettable_deletion_confirmation")
            ),
            eventSink = eventSink
        )()

        presenter.presentedCount().shouldBe(1)
        presenter.hasPresented { it.kind == "forgettable_deletion_confirmation" }.shouldBeTrue()
        story.moments.shouldBeEmpty()
    }

    @Test
    fun `Don't delete the moment if the user don't confirm the request`() {
        DeleteMomentUseCase(
            momentId = moment.id,
            stories = stories,
            presenter = presenter,
            eventSource = RecordedEventSource(
                ModalDismissalEvent("forgettable_deletion_confirmation")
            ),
            eventSink = eventSink
        )()

        presenter.presentedCount().shouldBe(1)
        presenter.hasPresented { it.kind == "forgettable_deletion_confirmation" }.shouldBeTrue()
        story.moments.shouldNotBeEmpty()
    }

    @Test
    fun `Informs the user if the moment could not be found`() {
        listOf(
            ModalApprovalEvent("forgettable_not_found_notification"),
            ModalDismissalEvent("forgettable_not_found_notification")
        ).forEach { event ->
            DeleteMomentUseCase(
                momentId = INVALID_UUID,
                stories = stories,
                presenter = presenter,
                eventSource = RecordedEventSource(event),
                eventSink = eventSink
            )()

            presenter.hasPresented { it.kind == "forgettable_not_found_notification" }.shouldBeTrue()
            story.moments.shouldNotBeEmpty()
        }
    }

    @Test
    fun `Does nothing when given an unsupported event`() {
        DeleteMomentUseCase(
            momentId = INVALID_UUID,
            stories = stories,
            presenter = presenter,
            eventSource = RecordedEventSource(
                ModalApprovalEvent("foo"),
                ModalDismissalEvent("foo"),
                UnsupportedEvent(),
                CompletionEvent()
            ),
            eventSink = eventSink
        )()
    }
}
