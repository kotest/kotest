package com.sksamuel.kotest.property.shrinking

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAtLeastOne
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropertyTesting
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.IntRangeShrinker
import io.kotest.property.arbitrary.intRange
import io.kotest.property.checkAll
import io.kotest.property.internal.doShrinking
import io.kotest.property.rtree

@EnabledIf(NotMacOnGithubCondition::class)
class IntRangeShrinkerTest : FunSpec({
   fun List<Int>.asRange() = first()..last()

   val state = PropertyTesting.shouldPrintShrinkSteps

   beforeSpec {
      PropertyTesting.shouldPrintShrinkSteps = false
   }

   afterSpec {
      PropertyTesting.shouldPrintShrinkSteps = state
   }

   test("IntRangeShrinker should include input minus head") {
      checkAll(Arb.intRange(0..10)) { range ->
         if (range.count() > 1) {
            val candidates = IntRangeShrinker().shrink(range)
            val shrunk = range.drop(1)
            candidates.forAtLeastOne {
               shrunk.asRange() shouldBe it
            }
         }
      }
   }

   test("IntRangeShrinker should include input minus tail") {
      checkAll(Arb.intRange(0..10)) { range ->
         if (range.count() > 1) {
            val candidates = IntRangeShrinker().shrink(range)
            val shrunk = range.toList().dropLast(1)
            candidates.forAtLeastOne {
               shrunk.asRange() shouldBe it
            }
         }
      }
   }

   test("IntRangeShrinker should shrink to expected value") {
      checkAll(Arb.intRange(0..10)) { range ->
         if (!range.isEmpty()) {
            val shrinks = IntRangeShrinker().rtree(range)
            val shrunk = doShrinking(shrinks, ShrinkingMode.Unbounded) {
               it shouldHaveSize 0
            }
            shrunk.shrink shouldHaveSize 1
         }
      }

      val shrinks = IntRangeShrinker().rtree(0..10)
      val shrunk = doShrinking(shrinks, ShrinkingMode.Unbounded) {
         it shouldHaveAtMostSize 2
      }
      shrunk.shrink shouldHaveSize 3
   }
})
