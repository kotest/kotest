package com.sksamuel.kotest.property.shrinking

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.IterationSkippedException
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.RTree
import io.kotest.property.ShrinkingMode
import io.kotest.property.assume
import io.kotest.property.internal.doShrinking

/**
 * Regression tests for https://github.com/kotest/kotest/issues/5693
 *
 * The shrinker must not treat an IterationSkippedException (raised by assume()) as a test failure.
 * A shrunk candidate that fails an assumption is not a valid counterexample and should be skipped,
 * not used as the "smallest failing case".
 */
class AssumeShrinkingTest : FunSpec() {
   init {
      test("doShrinking should treat assume() failures as passes, not as test failures") {
         // Manual shrink tree: initial value 10, shrink candidates are 0, 3, and 7.
         // assume() requires n >= 5, so candidates 0 and 3 trigger IterationSkippedException.
         // Candidate 7 is the genuine smallest failure (passes assumption, fails property).
         val result = doShrinking(
            RTree({ 10 }, lazy {
               listOf(
                  RTree({ 0 }),  // fails assume(n >= 5) → IterationSkippedException
                  RTree({ 3 }),  // fails assume(n >= 5) → IterationSkippedException
                  RTree({ 7 }),  // passes assume, fails property (n > 5)
               )
            }),
            ShrinkingMode.Unbounded
         ) { n: Int ->
            assume(n >= 5)
            if (n > 5) throw AssertionError("n must not exceed 5, but was $n")
         }

         // Before the fix, 0 was reported (IterationSkippedException treated as failure).
         // After the fix, 7 is correctly reported as the smallest genuine counterexample.
         result.shrink shouldBe 7
         result.cause.shouldBeInstanceOf<AssertionError>()
      }
   }
}
