package com.sksamuel.kotest.engine.test

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.source.SourceRef.ClassSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.Materializer
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.comparables.shouldBeLessThan
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

@EnabledIf(LinuxCondition::class)
class TestCaseSourceRefTest : FunSpec() {
   init {
      test("source ref should include file name and line number") {
         val tests = Materializer().materialize(MySpecForTestCaseSourceRefTest())
         tests.map { it.source }.shouldContainExactly(
            ClassSource("com.sksamuel.kotest.engine.test.MySpecForTestCaseSourceRefTest", 14),
            ClassSource("com.sksamuel.kotest.engine.test.MySpecForTestCaseSourceRefTest", 17),
         )
      }

      test("source ref should be performant").config(timeout = 240.seconds) {
         val duration = measureTime {
            repeat(100_000) {
               Materializer().materialize(MySpecForTestCaseSourceRefTest()).first().source
            }
         }
         duration shouldBeLessThan 240.seconds
      }
   }
}
