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

package com.hadisatrio.apps.kotlin.journal3.alert

import com.hadisatrio.apps.kotlin.journal3.Router
import com.hadisatrio.apps.kotlin.journal3.datetime.Timestamp
import com.hadisatrio.apps.kotlin.journal3.event.RecordedEventSource
import com.hadisatrio.apps.kotlin.journal3.event.UnsupportedEvent
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.libs.kotlin.foundation.event.CancellationEvent
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.modal.ModalApprovalEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import org.junit.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class AlertInactivityUseCaseTest {

    private val presenter = mockk<Presenter<Modal>>(relaxed = true)
    private val stories = FakeStories()
    private val story = stories.new()
    private val moment = story.moments.new()
    private val router = mockk<Router>(relaxed = true)

    @Test
    fun `Presents a modal should last written moment timestamp exceeds threshold`() {
        moment.update(Timestamp(Clock.System.now() - 1.days))

        AlertInactivityUseCase(
            threshold = 3.hours,
            stories = stories,
            presenter = presenter,
            eventSource = RecordedEventSource(
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true),
            router = mockk(relaxed = true)
        )()

        verify(exactly = 1) { presenter.present(withArg { it.kind.shouldBe("inactivity_alert") }) }
    }

    @Test
    fun `Presents a modal should no moments have ever been written`() {
        AlertInactivityUseCase(
            threshold = 3.hours,
            stories = stories,
            presenter = presenter,
            eventSource = RecordedEventSource(
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true),
            router = mockk(relaxed = true)
        )()

        verify(exactly = 1) { presenter.present(withArg { it.kind.shouldBe("inactivity_alert") }) }
    }

    @Test
    fun `Does nothing should last written moment timestamp is under threshold`() {
        moment.update(Timestamp(Clock.System.now() - 1.hours))

        AlertInactivityUseCase(
            threshold = 3.hours,
            stories = stories,
            presenter = presenter,
            eventSource = RecordedEventSource(
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true),
            router = mockk(relaxed = true)
        )()

        verify(inverse = true) { presenter.present(any()) }
    }

    @Test
    fun `Routes to the moment editor when receiving modal approval for 'inactivity_alert'`() {
        moment.update(Timestamp(Clock.System.now() - 1.days))

        AlertInactivityUseCase(
            threshold = 3.hours,
            stories = stories,
            presenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                ModalApprovalEvent("inactivity_alert"),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true),
            router = router
        )()

        verify(exactly = 1) { router.toMomentEditor(story.id) }
    }

    @Test(timeout = 5_000)
    fun `Stops upon receiving cancellation events`() {
        moment.update(Timestamp(Clock.System.now() - 1.days))

        listOf(CancellationEvent("user"), CancellationEvent("system")).forEach { event ->
            AlertInactivityUseCase(
                threshold = 3.hours,
                stories = stories,
                presenter = mockk(relaxed = true),
                eventSource = RecordedEventSource(event),
                eventSink = mockk(relaxed = true),
                router = mockk(relaxed = true)
            )()
        }
    }

    @Test
    fun `Ignores unknown events without repercussions`() {
        moment.update(Timestamp(Clock.System.now() - 1.days))

        AlertInactivityUseCase(
            threshold = 3.hours,
            stories = stories,
            presenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                ModalApprovalEvent("foo"),
                UnsupportedEvent(),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true),
            router = mockk(relaxed = true)
        )()
    }
}
