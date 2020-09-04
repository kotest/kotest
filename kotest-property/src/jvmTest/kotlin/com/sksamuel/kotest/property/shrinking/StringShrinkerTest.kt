package com.sksamuel.kotest.property.shrinking

import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.PropertyTesting
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.StringShrinker
import io.kotest.property.checkAll
import io.kotest.property.internal.doShrinking
import io.kotest.property.rtree

class StringShrinkerTest : StringSpec({

   beforeSpec {
      PropertyTesting.shouldPrintShrinkSteps = false
   }

   afterSpec {
      PropertyTesting.shouldPrintShrinkSteps = true
   }

   "StringShrinker should include bisected input" {
      checkAll { a: String ->
         if (a.length > 1) {
            val candidates = StringShrinker.shrink(a)
            candidates.forAtLeastOne {
               it.shouldHaveLength(a.length / 2 + a.length % 2)
            }
            candidates.forAtLeastOne {
               it.shouldHaveLength(a.length / 2)
            }
         }
      }
   }

   "StringShrinker should include 2 padded 'a's" {
      checkAll { a: String ->
         if (a.length > 1) {
            val candidates = StringShrinker.shrink(a)
            candidates.forAtLeastOne {
               it.shouldEndWith("a".repeat(a.length / 2))
            }
            candidates.forAtLeastOne {
               it.shouldStartWith("a".repeat(a.length / 2))
            }
         }
      }
   }

   "StringShrinker should shrink to expected value" {
      val prt = PropertyTesting.shouldPrintShrinkSteps
      PropertyTesting.shouldPrintShrinkSteps = false

      checkAll<String> { a ->

         val shrinks = StringShrinker.rtree(a)
         val shrunk = doShrinking(shrinks, ShrinkingMode.Unbounded) {
            it.shouldNotContain("#")
         }

         if (a.contains("#")) {
            shrunk.shrink shouldBe "#"
         } else {
            shrunk.shrink shouldBe a
         }
      }

      PropertyTesting.shouldPrintShrinkSteps = prt
   }

   "StringShrinker should prefer padded values" {
      val prt = PropertyTesting.shouldPrintShrinkSteps
      PropertyTesting.shouldPrintShrinkSteps = false

      val a = "97asd!@#ASD'''234)*safmasd"
      val shrinks = StringShrinker.rtree(a)
      doShrinking(shrinks, ShrinkingMode.Unbounded) {
         it.length.shouldBeLessThan(3)
      }.shrink shouldBe "aaa"

      doShrinking(shrinks, ShrinkingMode.Unbounded) {
         it.length.shouldBeLessThan(8)
      }.shrink shouldBe "aaaaaaaa"

      PropertyTesting.shouldPrintShrinkSteps = prt
   }
})
