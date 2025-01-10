package com.sksamuel.kotest.inspectors

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.filterMatching
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.ints.shouldBeEven
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.sequences.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.seconds

class FilterMatchingTest : FunSpec({
   test("Filtering empty list should return empty list") {
      emptyList<String>().filterMatching {
         it shouldHaveLength 5
      } shouldBe emptyList()
   }

   test("Filtering should only return matching elements") {
      arrayOf("a", "bb", "ccc", "dddd").filterMatching {
         it shouldHaveLength 1
      } shouldBe listOf("a")
   }

   test("Filtering applies all assertions to each element") {
      arrayOf("a", "b", "bb", "ccc", "dddd").filterMatching {
         it shouldHaveLength 1
         it shouldNotContain "b"
      } shouldBe listOf("a")
   }

   test("Filtering should work in assertSoftly context") {
      assertSoftly {
         1 shouldBe 1
         arrayOf(1, 2, 3).filterMatching { it.shouldBeEven() } shouldBe listOf(2)
         listOf(1, 2, 3).filterMatching { it.shouldBeEven() } shouldBe listOf(2)

         sequenceOf(1, 2, 3)
            .filterMatching { it.shouldBeEven() }
            .shouldBeInstanceOf<Sequence<Int>>()
            .shouldContainExactly(2)
      }
   }

   test("Filtering sequences should be done lazily").config(invocationTimeout = 1.seconds) {
      // Generating an infinite sequence. If the filtering is not done lazily, this test will timeout
      generateSequence(1) { it + 1 }
         .filterMatching { it.shouldBeEven() }
         .take(100)
         .toList() shouldContainExactly (2..200 step 2).toList()
   }
})
