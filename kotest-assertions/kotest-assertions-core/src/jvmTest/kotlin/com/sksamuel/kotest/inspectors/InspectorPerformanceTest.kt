package com.sksamuel.kotest.inspectors

import io.kotest.core.annotation.Issue
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forNone
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.longs.shouldBeLessThan
import kotlin.time.measureTime

@Issue("https://github.com/kotest/kotest/issues/5728")
class InspectorPerformanceTest : FunSpec({

   val list = (1..100_000).toList()

   test("forNone on 100k elements should take similar time to forAll") {
      // forAll passes when all elements satisfy the predicate (inner assertion passes, no error created)
      val forAllTime = measureTime {
         list.forAll { it shouldBeGreaterThan 0 }
      }

      // forNone passes when no elements satisfy the predicate (inner assertion fails for every element)
      // Without the LazyAssertionError optimisation, this was ~100x slower than forAll because
      // it created a full AssertionFailedError (with JVM stack trace capture) for every element.
      val forNoneTime = measureTime {
         list.forNone { it shouldBeGreaterThan 100_000 }
      }

      // forNone must complete within 10x the time of forAll.
      // In practice the optimized path is within ~2x; 10x gives headroom for CI variance.
      val maxAllowedNanos = forAllTime.inWholeNanoseconds * 10
      forNoneTime.inWholeNanoseconds shouldBeLessThan maxAllowedNanos
   }
})
