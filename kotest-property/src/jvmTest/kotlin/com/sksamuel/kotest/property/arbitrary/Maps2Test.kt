package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FreeSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeZero
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.Maps2Result
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map2
import io.kotest.property.arbitrary.take
import io.kotest.property.arbitrary.unit
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive

class Maps2Test : FreeSpec({

   "shared keys" - {
      val result = givenSamples(theArb(withSharedKeysPercent = 0..100, withSizeRange = 0..100))
         .map { it.value.left.keys.intersect(it.value.right.keys).size }
         .toList()

      "at least one sample should share no keys" {
         result.forAtLeastOne { it.shouldBeZero() }
      }

      "at least one sample should share all keys" {
         result.forAtLeastOne { it shouldBe 100 }
      }

      "number of shared keys should be between min and max percentage" {
         result.forAll {
            it.shouldBeBetween(0, 100)
         }
      }
   }

   "sizes" - {
      val result = givenSamples(theArb(withSizeRange = 50..150))
         .map { it.value.left.size to it.value.right.size }

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

      "for some left side should be bigger (non max)" {
         result.forAtLeastOne {
            it.first.shouldBeLessThan(150)
            it.first.shouldBeGreaterThan(it.second)
         }
      }

      "for some right side should be bigger (non max)" {
         result.forAtLeastOne {
            it.second.shouldBeLessThan(150)
            it.second.shouldBeGreaterThan(it.first)
         }
      }
   }

   "edgecases" - {
      "should contain sample with two empty maps (with minSize = 0)" {
         theArb(withSizeRange = 0..100).edgecases().forAtLeastOne {
            it.left.shouldBeEmpty()
            it.right.shouldBeEmpty()
         }
      }

      "should contain sample where one map has maxSize and the other map is empty (+ vice versa)" {
         givenSamples(theArb(withSizeRange = 0..100))
            .forAtLeastOne {
               it.value.left.shouldHaveSize(100)
               it.value.right.shouldBeEmpty()
            }.forAtLeastOne {
               it.value.left.shouldBeEmpty()
               it.value.right.shouldHaveSize(100)
            }
      }

      "should contain sample where both maps have the same size (not max, not empty) and share all keys" {
         givenSamples(theArb(withSizeRange = 0..100))
            .forAtLeastOne {
               it.value.left.size.shouldBeLessThan(100)
               it.value.left.size.shouldBeGreaterThan(0)
               it.value.left.keys shouldBe it.value.right.keys
            }
      }

      "should contain sample where both maps have maxSize and share all keys" {
         givenSamples(theArb(withSizeRange = 0..100))
            .forAtLeastOne {
               it.value.left.size shouldBe 100
               it.value.left.keys shouldBe it.value.right.keys
            }
      }

      "should contain sample where both maps have maxSize and share no keys" {
         givenSamples(theArb(withSizeRange = 0..100))
            .forAtLeastOne {
               it.value.left.size shouldBe 100
               it.value.right.size shouldBe 100

               it.value.left.keys.intersect(it.value.right.keys).shouldBeEmpty()
            }
      }

      "should contain sample where both maps have minSize and share all keys" {
         givenSamples(theArb(withSizeRange = 50..100))
            .forAtLeastOne {
               it.value.left.size shouldBe 50
               it.value.left.keys shouldBe it.value.right.keys
            }
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


   "intersect size" {
      val arb = theArb(withSizeRange = 10..100)
      checkAll(PropTestConfig(outputClassifications = true), arb) { (a, b) ->
         val size = a.keys.intersect(b.keys).size
         collect("intersection", size)
         collect("mapA", a.keys.size)
         collect("mapB", b.keys.size)
      }
   }

   "can use exhaustive" {
      Arb.map2(
         genK = (0..10).toList().exhaustive(),
         genA = Arb.boolean(),
         genB = Arb.boolean(),
         size = 0..10
      ).take(6).forEach {
         println(it)
      }
   }
})

private typealias SampleForTest = Maps2Result<Int, Unit, Unit>

private fun givenSamples(withArb: Arb<SampleForTest>) =
   withArb.generate(RandomSource.default()).take(2000).toList()

private fun theArb(withSizeRange: IntRange = 0..1000, withSharedKeysPercent: IntRange = 0..100) =
   Arb.map2(
      genK = Arb.int(),
      genA = Arb.unit(),
      genB = Arb.unit(),
      size = withSizeRange,
      shared = withSharedKeysPercent
   )
