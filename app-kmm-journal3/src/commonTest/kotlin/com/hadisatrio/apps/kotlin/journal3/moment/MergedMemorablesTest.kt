package com.hadisatrio.apps.kotlin.journal3.moment

import com.benasher44.uuid.uuid4
import com.hadisatrio.apps.kotlin.journal3.moment.fake.FakeMemorable
import com.hadisatrio.apps.kotlin.journal3.moment.fake.FakeMemorables
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import kotlin.test.Test

class MergedMemorablesTest {

    private val collection = MergedMemorables(
        FakeMemorables("places"),
        FakeMemorables("people")
    )

    @Test
    fun `Establishes relation if it is supported by the contained memorables`() {
        val momentId = uuid4()

        val oneMemorableId = uuid4()
        collection.relate(momentId, FakeMemorable("places", oneMemorableId))
        collection.relevantTo(momentId).shouldHaveSize(1)
        collection.find(oneMemorableId).shouldHaveSize(1)

        val otherMemorableId = uuid4()
        collection.relate(momentId, FakeMemorable("people", otherMemorableId))
        collection.relevantTo(momentId).shouldHaveSize(2)
        collection.find(otherMemorableId).shouldHaveSize(1)
    }

    @Test
    fun `Finds a memorable by its ID`() {
        val momentId = uuid4()
        val memorableId = uuid4()
        collection.relate(momentId, FakeMemorable("places", memorableId))

        collection.find(memorableId).shouldHaveSize(1)
        collection.find(uuid4()).shouldBeEmpty()
    }

    @Test
    fun `Throws when asked to relate unknown objects`() {
        val momentId = uuid4()

        shouldThrow<IllegalArgumentException> {
            collection.relate(momentId, FakeMemorable("music", uuid4()))
        }
        shouldThrow<IllegalArgumentException> {
            collection.relate(momentId, "photo")
        }
    }
}
