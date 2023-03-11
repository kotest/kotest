package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map2
import io.kotest.property.arbitrary.unit
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive

class Maps2Test : FreeSpec({


   "shared keys" - {
      val result = givenSamples(theArb(withSharedKeysPercent = 0..100, withSizeRange = 0..100))
         .map { it.value.first.keys.intersect(it.value.second.keys).size }
         .toList()

      "at least one sample should share no keys" {
         result.forAtLeastOne { it.shouldBeZero() }
      }

      "at least one sample should share all keys" {
         result.forAtLeastOne { it shouldBe 100 }
      }

      "all should be in between" {
         result.forAll {
            it.shouldBeBetween(0, 100)
         }
      }
   }

   "sizes" - {
      val result = givenSamples(theArb(withSizeRange = 50..150))
         .map { it.value.first.size to it.value.second.size }

      "all samples should respect the min size" {
         result.forAll {
            it.first.shouldBeGreaterThanOrEqual(50)
            it.second.shouldBeGreaterThanOrEqual(50)
         }
      }

      "all samples should respect the max size" {
         result.forAll {
            it.first.shouldBeLessThanOrEqualTo(150)
            it.second.shouldBeLessThanOrEqualTo(150)
         }
      }
   }

   "edgecases" - {
      "should contain 2 empty maps" {
         theArb().edgecases().forAtLeastOne {
            it.first.shouldBeEmpty()
            it.second.shouldBeEmpty()
         }
      }

      "one map should have maxSize while the other map should be empty" {
         givenSamples(theArb(withSizeRange = 0..100))
            .forAtLeastOne {
               it.value.first.shouldHaveSize(100)
               it.value.second.shouldBeEmpty()
            }.forAtLeastOne {
               it.value.first.shouldBeEmpty()
               it.value.second.shouldHaveSize(100)
            }
      }

      "both maps should have the same size and share all keys" {
         givenSamples(theArb(withSizeRange = 0..100))
            .forAtLeastOne {
               it.value.first.size.shouldBeLessThan(100)
               it.value.first.keys shouldBe it.value.second.keys
            }
      }

      "both maps should have maxSize and share all keys" {
         givenSamples(theArb(withSizeRange = 0..100))
            .forAtLeastOne {
               it.value.first.size shouldBe 100
               it.value.first.keys shouldBe it.value.second.keys
            }
      }
   }

   "intersect size" {
      val arb = theArb()
      checkAll(arb) { (a, b) ->
         val size = a.keys.intersect(b.keys).size
         collect("intersection", size)
         collect("mapA", a.keys.size)
         collect("mapB", b.keys.size)
      }
   }

   "shrinker" {
      val arb = theArb()

      shouldFail {
         checkAll(arb) { (a, b) ->
            a.size.shouldBeLessThan(5)
         }
      }
   }
})

typealias SampleType = Pair<Map<Int, Unit>, Map<Int, Unit>>

private fun givenSamples(withArb: Arb<SampleType>) =
   withArb.generate(RandomSource.default()).take(1000).toList()

private fun theArb(withSizeRange: IntRange = 0..1000, withSharedKeysPercent: IntRange = 0..100) = Arb.map2(
   keyArb = Arb.int(),
   arbA = Arb.unit(),
   arbB = Arb.unit(),
   sizeRange = withSizeRange,
   sharedKeyPercentage = withSharedKeysPercent
)

