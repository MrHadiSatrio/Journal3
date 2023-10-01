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

import com.benasher44.uuid.Uuid
import com.hadisatrio.apps.kotlin.journal3.forgettable.DeleteForgettableUseCase
import com.hadisatrio.apps.kotlin.journal3.forgettable.Forgettable
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.libs.kotlin.foundation.event.EventSink
import com.hadisatrio.libs.kotlin.foundation.event.RxEventSource
import com.hadisatrio.libs.kotlin.foundation.modal.Modal
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenter

class DeleteMomentUseCase(
    private val momentId: Uuid,
    private val stories: Stories,
    presenter: Presenter<Modal>,
    eventSource: RxEventSource,
    eventSink: EventSink
) : DeleteForgettableUseCase(presenter, eventSource, eventSink) {

    override fun forgettable(): Forgettable? {
        return stories.findMoment(momentId).firstOrNull()
    }
}
