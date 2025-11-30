package com.sksamuel.kotest.runner.junit5

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.testkit.engine.EngineTestKit
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class PerformanceTest : FunSpec() {
   init {
      test("performance of multiple tests").config(timeout = 10.seconds) {
         EngineTestKit
            .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
            .selectors(DiscoverySelectors.selectClass(ManyTests::class.java))
            .configurationParameter("allow_private", "true")
            .execute()
            .allEvents().apply {
               finished().count() shouldBe 1003L // kotest, spec, foo, and the nested tests
            }
      }
   }
}

@EnabledIf(LinuxOnlyGithubCondition::class)
private class ManyTests : DescribeSpec() {
   init {
      describe("foo") {
         List(1000) { it }.forEach {
            it("test $it") {
               1 shouldBe 1
            }
         }
      }
   }
}


