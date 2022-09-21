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

import com.hadisatrio.apps.kotlin.journal3.Router
import com.hadisatrio.apps.kotlin.journal3.event.RecordedEventSource
import com.hadisatrio.apps.kotlin.journal3.event.UnsupportedEvent
import com.hadisatrio.apps.kotlin.journal3.id.FakeTargetId
import com.hadisatrio.libs.kotlin.foundation.event.CompletionEvent
import com.hadisatrio.libs.kotlin.foundation.event.SelectionEvent
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class ShowStoryUseCaseTest {

    private val stories = SelfPopulatingStories(
        noOfStories = 1,
        noOfMoments = 1,
        origin = FakeStories()
    )

    @Test
    fun `Forwards story to the presenter`() {
        val story = stories.first()
        val targetId = FakeTargetId(story.id)
        val presenter = mockk<Presenter<Story>>(relaxed = true)

        ShowStoryUseCase(
            targetId = targetId,
            stories = stories,
            presenter = presenter,
            eventSource = RecordedEventSource(CompletionEvent()),
            eventSink = mockk(relaxed = true),
            router = mockk(relaxed = true)
        )()

        verify(exactly = 1) { presenter.present(story) }
    }

    @Test
    fun `Routes to the editor when action 'add' is selected`() {
        val story = stories.first()
        val targetId = FakeTargetId(story.id)
        val router = mockk<Router>(relaxed = true)

        ShowStoryUseCase(
            targetId = targetId,
            stories = stories,
            presenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                SelectionEvent("action", "add"),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true),
            router = router
        )()

        verify(exactly = 1) { router.toMomentEditor() }
    }

    @Test
    fun `Routes to the editor when action 'edit' is selected`() {
        val story = stories.first()
        val targetId = FakeTargetId(story.id)
        val router = mockk<Router>(relaxed = true)

        ShowStoryUseCase(
            targetId = targetId,
            stories = stories,
            presenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                SelectionEvent("action", "edit"),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true),
            router = router
        )()

        verify(exactly = 1) { router.toStoryEditor(story.id) }
    }

    @Test
    fun `Does nothing when given an unsupported event`() {
        val story = stories.first()
        val targetId = FakeTargetId(story.id)

        ShowStoryUseCase(
            targetId = targetId,
            stories = stories,
            presenter = mockk(relaxed = true),
            eventSource = RecordedEventSource(
                UnsupportedEvent(),
                CompletionEvent()
            ),
            eventSink = mockk(relaxed = true),
            router = mockk(relaxed = true)
        )()
    }
}
