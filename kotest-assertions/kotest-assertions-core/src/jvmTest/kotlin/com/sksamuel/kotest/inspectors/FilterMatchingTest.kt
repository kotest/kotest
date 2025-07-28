package com.sksamuel.kotest.inspectors

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.filterMatching
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.ints.shouldBeEven
import io.kotest.matchers.sequences.shouldBeEmpty
import io.kotest.matchers.sequences.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeInstanceOf

class FilterMatchingTest : FunSpec({
    test("Filtering empty list or array should return empty list") {
        emptyList<String>().filterMatching { it shouldBe "foo" } shouldBe emptyList()
        emptyArray<String>().filterMatching { it shouldBe "foo" } shouldBe emptyList()
    }

    test("Filtering empty sequence should return an empty sequence") {
        emptySequence<String>()
            .filterMatching { it shouldBe "foo" }
            .shouldBeInstanceOf<Sequence<String>>()
            .shouldBeEmpty()
    }

    test("Filtering should only return matching elements") {
        arrayOf("a", "bb", "ccc", "dddd").filterMatching { it shouldHaveLength 1 } shouldBe listOf("a")
        listOf("a", "bb", "ccc", "dddd").filterMatching { it shouldHaveLength 1 } shouldBe listOf("a")

        sequenceOf("a", "bb", "ccc", "dddd")
            .filterMatching { it shouldHaveLength 1 }
            .shouldBeInstanceOf<Sequence<String>>()
            .shouldContainExactly("a")
    }

    test("Filtering applies all assertions to each element") {
        val assertion: (String) -> Unit = {
            it shouldHaveLength 1
            it shouldNotContain "b"
        }

        arrayOf("a", "b", "bb", "ccc", "dddd").filterMatching(assertion) shouldBe listOf("a")
        listOf("a", "b", "bb", "ccc", "dddd").filterMatching(assertion) shouldBe listOf("a")

        sequenceOf("a", "b", "bb", "ccc", "dddd")
            .filterMatching(assertion)
            .shouldBeInstanceOf<Sequence<String>>()
            .shouldContainExactly("a")
    }

    test("Filtering should work in assertSoftly context when all assertions pass") {
        shouldNotThrowAny {
            assertSoftly {
                arrayOf(1, 2, 3).filterMatching { it.shouldBeEven() } shouldBe listOf(2)
                listOf(1, 2, 3).filterMatching { it.shouldBeEven() } shouldBe listOf(2)

                sequenceOf(1, 2, 3)
                    .filterMatching { it.shouldBeEven() }
                    .shouldBeInstanceOf<Sequence<Int>>()
                    .shouldContainExactly(2)
            }
        }
    }

    test("Filtering in assertSoftly block should work when assertions fail") {
        shouldFail {
            assertSoftly {
                arrayOf(1, 2, 3).filterMatching { it.shouldBeEven() }.shouldBeEmpty()
                listOf(1, 2, 3).filterMatching { it.shouldBeEven() }.shouldBeEmpty()

                sequenceOf(1, 2, 3)
                    .filterMatching { it.shouldBeEven() }
                    .shouldBeInstanceOf<Sequence<Int>>()
                    .shouldBeEmpty()
            }
        }.message.shouldContainInOrder(
            "The following 3 assertions failed:",
            "1) List should be empty but has 1 elements, first being: 2",
            "2) List should be empty but has 1 elements, first being: 2",
            "3) Sequence should be empty but has at least one element, first being: 2"
        )
    }

    test("Filtering all elements should return empty list") {
        listOf("foo", "bar").filterMatching {
            it shouldBe "baz"
        } shouldBe emptyList()
    }

    test("Filtering sequences should be done lazily") {
        shouldNotThrowAny {
            sequence<Int> {
                (1..100).forEach { yield(it) }
                error("Should not be evaluated") // If the filtering is not done lazily, this will throw
            }.filterMatching { it.shouldBeEven() }
        }
    }
})
