package com.sksamuel.kotest.engine.test

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.Materializer
import io.kotest.matchers.collections.shouldContainExactly
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class TestCaseSourceRefTest : FunSpec() {
   init {
      test("ClassLineSource should include file name and line number") {
         val tests = Materializer().materialize(MySpecForTestCaseSourceRefTest())
         tests.map { it.source }.shouldContainExactly(
            SourceRef.ClassLineSource("com.sksamuel.kotest.engine.test.MySpecForTestCaseSourceRefTest", 14),
            SourceRef.ClassLineSource("com.sksamuel.kotest.engine.test.MySpecForTestCaseSourceRefTest", 17),
         )
      }

      test("source ref should be performant").config(timeout = 20.seconds) {
         repeat(5_000) {
            Materializer().materialize(MySpecForTestCaseSourceRefTest()).first().source
         }
      }
   }
}
