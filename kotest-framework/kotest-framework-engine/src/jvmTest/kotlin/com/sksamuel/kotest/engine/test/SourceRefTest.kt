package com.sksamuel.kotest.engine.test

import io.kotest.common.reflection.bestName
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.source.SourceRef
import io.kotest.core.source.SourceRefUtils
import io.kotest.core.spec.SpecRef.Reference
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.Materializer
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class SourceRefTest : FunSpec() {

   init {
      test("ClassLineSource should include file name and line number") {
         val spec = MySpecForTestCaseSourceRefTest()
         val tests = Materializer().materialize(spec, Reference(spec::class, spec::class.bestName()))
         tests.map { it.source }.shouldContainExactly(
            SourceRef.ClassLineSource("com.sksamuel.kotest.engine.test.MySpecForTestCaseSourceRefTest", 14),
            SourceRef.ClassLineSource("com.sksamuel.kotest.engine.test.MySpecForTestCaseSourceRefTest", 17),
         )
      }

      test("source ref should be performant").config(timeout = 20.seconds) {
         repeat(5_000) {
            val spec = MySpecForTestCaseSourceRefTest()
            Materializer().materialize(spec, Reference(spec::class, spec::class.bestName())).first().source
         }
      }
   }
}

class SourceRefUtilsTest : FreeSpec() {
   init {
      "should strip datatest frames" {
         val stack = arrayOf(
            StackTraceElement(
               "io.kotest.datatest.FunSpecContainerScopeKt",
               "withContexts",
               "funSpecContainerScope.kt",
               213
            ),
            StackTraceElement("io.kotest.core.source.SourceRefKt", "sourceRef", "sourceRef.kt", 45),
            StackTraceElement("com.sksamuel.kotest.filter.InterpreterTest", "invoke", "specs.kt", 67),
         )
         SourceRefUtils.firstUserFrame(stack)!!.className shouldBe "com.sksamuel.kotest.filter.InterpreterTest"
      }
   }
}
