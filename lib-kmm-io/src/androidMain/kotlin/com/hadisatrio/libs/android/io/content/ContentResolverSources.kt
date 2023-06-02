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

package com.hadisatrio.libs.android.io.content

import android.annotation.SuppressLint
import android.content.ContentResolver
import com.chrynan.uri.core.Uri
import com.hadisatrio.libs.android.io.uri.toAndroidUri
import com.hadisatrio.libs.kotlin.io.Sources
import okio.FileNotFoundException
import okio.Source
import okio.source

class ContentResolverSources(
    private val contentResolver: ContentResolver
) : Sources {

    @Suppress("FoldInitializerAndIfToElvis")
    @SuppressLint("Recycle")
    override fun open(uri: Uri): Source {
        val scheme = uri.scheme
        require(scheme == "content") { "Expected scheme to be 'content' but was '$scheme'." }

        val stream = contentResolver.openInputStream(uri.toAndroidUri())
        if (stream == null) throw FileNotFoundException("No content found for the given URI '$uri'.")
        return stream.source()
    }
}
