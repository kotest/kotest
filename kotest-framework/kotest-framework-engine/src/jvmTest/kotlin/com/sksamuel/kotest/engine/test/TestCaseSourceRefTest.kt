package com.sksamuel.kotest.engine.test

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.source.SourceRef.ClassSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.Materializer
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.comparables.shouldBeLessThan
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

class TestCaseSourceRefTest : FunSpec() {
   init {
      test("source ref should include file name and line number") {
         val tests = Materializer(ProjectConfiguration()).materialize(MySpecForTestCaseSourceRefTest())
         tests.map { it.source }.shouldContainExactly(
            ClassSource("com.sksamuel.kotest.engine.test.MySpecForTestCaseSourceRefTest", 11),
            ClassSource("com.sksamuel.kotest.engine.test.MySpecForTestCaseSourceRefTest", 14),
         )
      }

      test("source ref should be performant").config(timeout = 10.seconds) {
         val duration = measureTime {
            repeat(10_000) {
               Materializer(ProjectConfiguration()).materialize(MySpecForTestCaseSourceRefTest()).first().source
            }
         duration shouldBeLessThan 10.seconds
      }
   }
}
