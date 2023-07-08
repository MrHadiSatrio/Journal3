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

package com.hadisatrio.libs.android.foundation.widget

import android.widget.TextView
import androidx.test.runner.AndroidJUnit4
import io.kotest.matchers.shouldBe
import io.mockk.spyk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RuntimeEnvironment

@RunWith(AndroidJUnit4::class)
class TextViewStringPresenterTest {

    @Test
    fun `Presents given string to the TextView`() {
        val textView = spyk(TextView(RuntimeEnvironment.getApplication()))
        val presenter = TextViewStringPresenter(textView)

        presenter.present("Foo")

        textView.text.toString().shouldBe("Foo")
    }

    @Test
    fun `Prevents redundant set calls to the TextView`() {
        val textView = spyk(TextView(RuntimeEnvironment.getApplication()))
        val presenter = TextViewStringPresenter(textView)

        repeat(10) { presenter.present("Foo") }

        verify(exactly = 1) { textView.text = "Foo" }
    }
}
