package com.sksamuel.kotest.matchers.collections

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.monotonicallyDecreasing
import io.kotest.matchers.collections.monotonicallyDecreasingWith
import io.kotest.matchers.collections.monotonicallyIncreasing
import io.kotest.matchers.collections.monotonicallyIncreasingWith
import io.kotest.matchers.collections.shouldBeMonotonicallyDecreasing
import io.kotest.matchers.collections.shouldBeMonotonicallyDecreasingWith
import io.kotest.matchers.collections.shouldBeMonotonicallyIncreasing
import io.kotest.matchers.collections.shouldBeMonotonicallyIncreasingWith
import io.kotest.matchers.collections.shouldBeStrictlyDecreasing
import io.kotest.matchers.collections.shouldBeStrictlyDecreasingWith
import io.kotest.matchers.collections.shouldBeStrictlyIncreasing
import io.kotest.matchers.collections.shouldBeStrictlyIncreasingWith
import io.kotest.matchers.collections.shouldNotBeMonotonicallyDecreasing
import io.kotest.matchers.collections.shouldNotBeMonotonicallyDecreasingWith
import io.kotest.matchers.collections.shouldNotBeMonotonicallyIncreasing
import io.kotest.matchers.collections.shouldNotBeMonotonicallyIncreasingWith
import io.kotest.matchers.collections.shouldNotBeStrictlyDecreasing
import io.kotest.matchers.collections.shouldNotBeStrictlyDecreasingWith
import io.kotest.matchers.collections.shouldNotBeStrictlyIncreasing
import io.kotest.matchers.collections.shouldNotBeStrictlyIncreasingWith
import io.kotest.matchers.collections.strictlyDecreasing
import io.kotest.matchers.collections.strictlyDecreasingWith
import io.kotest.matchers.collections.strictlyIncreasing
import io.kotest.matchers.collections.strictlyIncreasingWith
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class IncreasingDecreasingTest : WordSpec() {

   private val asc = { a: Int, b: Int -> a - b }
   private val desc = { a: Int, b: Int -> b - a }

   init {
      "shouldBeIncreasing" should {
         "test that a list is monotonically increasing" {
            listOf(1, 2, 2, 3) shouldBe monotonicallyIncreasing<Int>()
            listOf(6, 5) shouldNotBe monotonicallyIncreasing<Int>()
            listOf(1, 2, 2, 3).shouldBeMonotonicallyIncreasing()
            listOf(6, 5).shouldNotBeMonotonicallyIncreasing()
         }
         "test that a sequence is monotonically increasing" {
            sequenceOf(1, 2, 2, 3).shouldBeMonotonicallyIncreasing()
            sequenceOf(6, 5).shouldNotBeMonotonicallyIncreasing()
         }
         "test that a list is monotonically increasing according to comparator" {
            val comparator = Comparator(desc)
            listOf(3, 2, 2, 1) shouldBe monotonicallyIncreasingWith(comparator)
            listOf(5, 6) shouldNotBe monotonicallyIncreasingWith(comparator)
            listOf(3, 2, 2, 1).shouldBeMonotonicallyIncreasingWith(comparator)
            listOf(5, 6).shouldNotBeMonotonicallyIncreasingWith(comparator)
         }
         "test that a sequence is monotonically increasing according to comparator" {
            val comparator = Comparator(desc)
            sequenceOf(3, 2, 2, 1).shouldBeMonotonicallyIncreasingWith(comparator)
            sequenceOf(5, 6).shouldNotBeMonotonicallyIncreasingWith(comparator)
         }
         "test that a list is strictly increasing" {
            listOf(1, 2, 3) shouldBe strictlyIncreasing<Int>()
            listOf(1, 2, 2, 3) shouldNotBe strictlyIncreasing<Int>()
            listOf(6, 5) shouldNotBe strictlyIncreasing<Int>()
            listOf(1, 2, 3).shouldBeStrictlyIncreasing()
            listOf(1, 2, 2, 3).shouldNotBeStrictlyIncreasing()
            listOf(6, 5).shouldNotBeStrictlyIncreasing()
         }
         "test that a sequence is strictly increasing" {
            sequenceOf(1, 2, 3).shouldBeStrictlyIncreasing()
            sequenceOf(1, 2, 2, 3).shouldNotBeStrictlyIncreasing()
            sequenceOf(6, 5).shouldNotBeStrictlyIncreasing()
         }
         "test that a list is strictly increasing according to comparator" {
            val comparator = Comparator(asc)
            listOf(1, 2, 3) shouldBe strictlyIncreasingWith(comparator)
            listOf(1, 2, 2, 3) shouldNotBe strictlyIncreasingWith(comparator)
            listOf(6, 5) shouldNotBe strictlyIncreasingWith(comparator)
            listOf(1, 2, 3).shouldBeStrictlyIncreasingWith(comparator)
            listOf(1, 2, 2, 3).shouldNotBeStrictlyIncreasingWith(comparator)
            listOf(6, 5).shouldNotBeStrictlyIncreasingWith(comparator)
         }
         "test that a sequence is strictly increasing according to comparator" {
            val comparator = Comparator(asc)
            sequenceOf(1, 2, 3).shouldBeStrictlyIncreasingWith(comparator)
            sequenceOf(1, 2, 2, 3).shouldNotBeStrictlyIncreasingWith(comparator)
            sequenceOf(6, 5).shouldNotBeStrictlyIncreasingWith(comparator)
         }
      }

      "shouldBeDecreasing" should {
         "test that a list is monotonically decreasing" {
            listOf(3, 2, 2, -4) shouldBe monotonicallyDecreasing<Int>()
            listOf(5, 6) shouldNotBe monotonicallyDecreasing<Int>()
            listOf(3, 2, 2, -4).shouldBeMonotonicallyDecreasing()
            listOf(5, 6).shouldNotBeMonotonicallyDecreasing()
         }
         "test that a sequence is monotonically decreasing" {
            sequenceOf(3, 2, 2, -4).shouldBeMonotonicallyDecreasing()
            sequenceOf(5, 6).shouldNotBeMonotonicallyDecreasing()
         }
         "test that a list is monotonically decreasing according to comparator" {
            val comparator = Comparator(desc)
            listOf(-4, 2, 2, 3) shouldBe monotonicallyDecreasingWith(comparator)
            listOf(6, 5) shouldNotBe monotonicallyDecreasingWith(comparator)
            listOf(-4, 2, 2, 3).shouldBeMonotonicallyDecreasingWith(comparator)
            listOf(6, 5).shouldNotBeMonotonicallyDecreasingWith(comparator)
         }
         "test that a sequence is monotonically decreasing according to comparator" {
            val comparator = Comparator(desc)
            sequenceOf(-4, 2, 2, 3).shouldBeMonotonicallyDecreasingWith(comparator)
            sequenceOf(6, 5).shouldNotBeMonotonicallyDecreasingWith(comparator)
         }
         "test that a list is strictly decreasing" {
            listOf(3, 2, -4) shouldBe strictlyDecreasing<Int>()
            listOf(3, 2, 2, -4) shouldNotBe strictlyDecreasing<Int>()
            listOf(5, 6) shouldNotBe strictlyDecreasing<Int>()
            listOf(3, 2, -4).shouldBeStrictlyDecreasing()
            listOf(3, 2, 2, -4).shouldNotBeStrictlyDecreasing()
            listOf(5, 6).shouldNotBeStrictlyDecreasing()
         }
         "test that a sequence is strictly decreasing" {
            sequenceOf(3, 2, -4).shouldBeStrictlyDecreasing()
            sequenceOf(3, 2, 2, -4).shouldNotBeStrictlyDecreasing()
            sequenceOf(5, 6).shouldNotBeStrictlyDecreasing()
         }
         "test that a list is strictly decreasing according to comparator" {
            val comparator = Comparator(desc)
            listOf(-4, 2, 3) shouldBe strictlyDecreasingWith(comparator)
            listOf(-4, 2, 2, 3) shouldNotBe strictlyDecreasingWith(comparator)
            listOf(6, 5) shouldNotBe strictlyDecreasingWith(comparator)
            listOf(-4, 2, 3).shouldBeStrictlyDecreasingWith(comparator)
            listOf(-4, 2, 2, 3).shouldNotBeStrictlyDecreasingWith(comparator)
            listOf(6, 5).shouldNotBeStrictlyDecreasingWith(comparator)
         }
         "test that a sequence is strictly decreasing according to comparator" {
            val comparator = Comparator(desc)
            sequenceOf(-4, 2, 3).shouldBeStrictlyDecreasingWith(comparator)
            sequenceOf(-4, 2, 2, 3).shouldNotBeStrictlyDecreasingWith(comparator)
            sequenceOf(6, 5).shouldNotBeStrictlyDecreasingWith(comparator)
         }
      }
   }
}
