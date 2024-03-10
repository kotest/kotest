package com.sksamuel.kotest.runner.junit5

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit
import kotlin.time.Duration.Companion.seconds

class PerformanceTest : FunSpec() {
   init {
      test("performance of multiple tests").config(timeout = 20.seconds) {
         EngineTestKit
            .engine("kotest")
            .selectors(DiscoverySelectors.selectClass(ManyTests::class.java)).execute()
            .allEvents().apply {
               finished().count() shouldBe 10003L // kotest, spec, foo, and the nested tests
            }
      }
   }
}

private class ManyTests : DescribeSpec() {
   init {
      describe("foo") {
         List(10000) { it }.forEach {
            it("test $it") {
               1 shouldBe 1
            }
         }
      }
   }
}


