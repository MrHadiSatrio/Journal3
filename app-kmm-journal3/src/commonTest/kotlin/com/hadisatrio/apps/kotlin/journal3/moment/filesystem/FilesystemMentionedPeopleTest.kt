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

package com.hadisatrio.apps.kotlin.journal3.moment.filesystem

import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.kotlin.journal3.token.Token
import com.hadisatrio.apps.kotlin.journal3.token.TokenableString
import com.hadisatrio.libs.kotlin.geography.NullIsland
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import okio.Path.Companion.toPath
import okio.buffer
import okio.fakefilesystem.FakeFileSystem
import okio.use
import kotlin.test.AfterTest
import kotlin.test.Test

class FilesystemMentionedPeopleTest {

    private val fileSystem = FakeFileSystem()
    private val path = "content".toPath()
    private val people = FilesystemMentionedPeople(fileSystem, path)

    @AfterTest
    fun `Closes all file streams`() {
        fileSystem.checkNoOpenFiles()
    }

    @Test
    fun `Writes updates to the filesystem`() {
        val person = people.remember(Token("@nahlito"))
        val filePath = "content/${person.id}".toPath()
        val fileContent = { fileSystem.source(filePath).buffer().use { it.readUtf8() } }

        fileSystem.exists(filePath).shouldBeTrue()

        person.slug.shouldBe(Token("@nahlito"))
        fileContent().shouldContain("nahlito")

        fileContent().shouldNotContain("Nahl Elfath")
        person.name.shouldBe("@nahlito")
        person.update("Nahl Elfath")
        person.name.shouldBe("Nahl Elfath")
        fileContent().shouldContain("Nahl Elfath")
    }

    @Test
    fun `Establishes relation by basing on the moment ID and the annotated text`() {
        val oneMomentId = uuid4()
        val otherMomentId = uuid4()

        people.relevantTo(oneMomentId).shouldBeEmpty()

        people.relate(
            oneMomentId,
            TokenableString("First mention. Hi @nahlito!")
        )
        people.count().shouldBe(1)
        people.relevantTo(oneMomentId).shouldHaveSize(1)

        people.relate(
            oneMomentId,
            TokenableString("Adding mentions. Went to the mall with @mima and @nahlito.")
        )
        people.count().shouldBe(2)
        people.relevantTo(oneMomentId).shouldHaveSize(2)

        people.relate(
            otherMomentId,
            TokenableString("Adding to other moment. @mima, @nahlito, and @slyv.")
        )
        people.count().shouldBe(3)
        people.relevantTo(otherMomentId).shouldHaveSize(3)

        people.relate(
            oneMomentId,
            TokenableString("Just @mima now. And #hashtag which should not be valid.")
        )
        people.count().shouldBe(3)
        people.relevantTo(oneMomentId).shouldHaveSize(1)
    }

    @Test
    fun `Throws when asked to relate unknown objects`() {
        shouldThrow<IllegalArgumentException> { people.relate(uuid4(), NullIsland) }
    }

    @Test
    fun `Finds a person by its ID`() {
        val person = people.remember(Token("@nahlito"))
        people.find(person.id).shouldNotBeEmpty()
        people.find(uuid4()).shouldBeEmpty()
    }
}
