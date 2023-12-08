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

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.hadisatrio.apps.android.journal3.Journal3Application
import com.hadisatrio.apps.android.journal3.R
import com.hadisatrio.apps.android.journal3.journal3Application
import com.hadisatrio.apps.android.journal3.sentiment.RemoteTextViewColorSentimentPresenter
import com.hadisatrio.apps.kotlin.journal3.moment.CountLimitingMoments
import com.hadisatrio.apps.kotlin.journal3.moment.Moment
import com.hadisatrio.apps.kotlin.journal3.moment.OrderRandomizingMoments
import com.hadisatrio.apps.kotlin.journal3.moment.SentimentRangedMoments
import com.hadisatrio.apps.kotlin.journal3.sentiment.PositiveishSentimentRange
import com.hadisatrio.apps.kotlin.journal3.sentiment.Sentiment
import com.hadisatrio.apps.kotlin.journal3.story.InitDeferringStories
import com.hadisatrio.apps.kotlin.journal3.story.Reflection
import com.hadisatrio.apps.kotlin.journal3.story.ShowStoriesUseCase
import com.hadisatrio.apps.kotlin.journal3.story.Stories
import com.hadisatrio.apps.kotlin.journal3.story.fake.FakeStories
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.android.foundation.widget.RemoteTextViewStringPresenter
import com.hadisatrio.libs.android.foundation.widget.RemoteViewsUpdatingPresenter
import com.hadisatrio.libs.kotlin.foundation.event.NoOpEventSource
import com.hadisatrio.libs.kotlin.foundation.presentation.AdaptingPresenter
import com.hadisatrio.libs.kotlin.foundation.presentation.Presenters

class PositiveReflectionsWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        widgetManager: AppWidgetManager,
        widgetIds: IntArray
    ) {
        widgetIds.forEach { updateAppWidget(context, widgetManager, it) }
    }

    private fun updateAppWidget(
        context: Context,
        widgetManager: AppWidgetManager,
        widgetId: Int
    ) {
        val application = context.journal3Application
        val views = RemoteViews(context.packageName, R.layout.widget_moment)
        val useCase = application.useCaseDecor.apply(
            ShowStoriesUseCase(
                stories = InitDeferringStories {
                    FakeStories(
                        Reflection(
                            title = "",
                            synopsis = TokenableString.EMPTY,
                            moments = CountLimitingMoments(
                                limit = 1,
                                origin = OrderRandomizingMoments(
                                    origin = SentimentRangedMoments(
                                        sentimentRange = PositiveishSentimentRange,
                                        origin = application.stories.moments
                                    )
                                )
                            )
                        )
                    )
                },
                presenter = Presenter(application, widgetId, widgetManager, views),
                eventSource = NoOpEventSource,
                eventSink = application.globalEventSink
            )
        )
        useCase()
    }

    private class Presenter(
        private val application: Journal3Application,
        private val appWidgetId: Int,
        private val appWidgetManager: AppWidgetManager,
        private val views: RemoteViews
    ) : com.hadisatrio.libs.kotlin.foundation.presentation.Presenter<Stories> {

        private val sentimentPresenter = AdaptingPresenter<Moment?, Sentiment>(
            adapter = { it?.sentiment ?: Sentiment.DEFAULT },
            origin = RemoteTextViewColorSentimentPresenter(views, R.id.sentiment_indicator)
        )
        private val timestampPresenter = AdaptingPresenter<Moment?, String>(
            adapter = {
                val decor = { application.timestampDecor }
                it?.timestamp?.let { decor().apply(it).toString() }.orEmpty()
            },
            origin = RemoteTextViewStringPresenter(views, R.id.timestamp_label)
        )
        private val descriptionPresenter = AdaptingPresenter<Moment?, String>(
            adapter = { it?.description?.toString().orEmpty() },
            origin = RemoteTextViewStringPresenter(views, R.id.description_label)
        )
        private val attachmentsPresenter = AdaptingPresenter<Moment?, String>(
            adapter = { it?.attachments?.let { "${it.count()} attachment(s)" }.orEmpty() },
            origin = RemoteTextViewStringPresenter(views, R.id.attachment_count_label)
        )
        private val placePresenter = AdaptingPresenter<Moment?, String>(
            adapter = { it?.place?.name.orEmpty() },
            origin = RemoteTextViewStringPresenter(views, R.id.place_label)
        )

        override fun present(thing: Stories) {
            RemoteViewsUpdatingPresenter<Stories>(
                widgetId = appWidgetId,
                widgetManager = appWidgetManager,
                remoteViews = views,
                origin = AdaptingPresenter(
                    adapter = { it.moments.firstOrNull() },
                    origin = Presenters(
                        sentimentPresenter,
                        timestampPresenter,
                        descriptionPresenter,
                        attachmentsPresenter,
                        placePresenter
                    )
                )
            ).present(thing)
        }
    }
}
