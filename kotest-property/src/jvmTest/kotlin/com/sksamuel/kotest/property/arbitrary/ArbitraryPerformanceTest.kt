package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.config.TestConfig
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take
import kotlin.time.measureTime

/**
 * Regression test for https://github.com/kotest/kotest/issues/4932
 *
 * Before the fix, each [arbitrary] sample call created an intermediate ArbitraryBuilder + anonymous
 * Arb wrapper that was immediately thrown away. For deeply-nested structures this multiplied with
 * every level, causing severe GC pressure and making 1000 samples of a large object take upward of
 * a minute on modern hardware.
 */
class ArbitraryPerformanceTest : FunSpec({

   // A 5-level deep object graph, each level with several fields, modelling the kind of
   // Avro / data-class hierarchy that triggered the original report.
   data class L5(val a: Int, val b: String)
   data class L4(val x: L5, val y: L5, val z: L5)
   data class L3(val p: L4, val q: L4)
   data class L2(val m: L3, val n: L3, val o: L3)
   data class L1(val i: L2, val j: L2)

   val l5Arb = arbitrary { L5(Arb.int().bind(), Arb.string(1..8).bind()) }
   val l4Arb = arbitrary { L4(l5Arb.bind(), l5Arb.bind(), l5Arb.bind()) }
   val l3Arb = arbitrary { L3(l4Arb.bind(), l4Arb.bind()) }
   val l2Arb = arbitrary { L2(l3Arb.bind(), l3Arb.bind(), l3Arb.bind()) }
   val l1Arb = arbitrary { L1(l2Arb.bind(), l2Arb.bind()) }

   test("generating 1000 samples of a 5-level nested arbitrary completes in under 10 seconds").config(TestConfig(retries = 5)) {
      val rs = RandomSource.default()
      val elapsed = measureTime {
         l1Arb.take(1000, rs).toList()
      }
      elapsed.inWholeSeconds shouldBeLessThan 10
   }
})
