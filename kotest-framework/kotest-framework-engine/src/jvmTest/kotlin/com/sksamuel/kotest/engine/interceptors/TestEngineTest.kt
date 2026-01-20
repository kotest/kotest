package com.sksamuel.kotest.engine.interceptors

import com.sksamuel.kotest.engine.active.BangDisableFunSpec
import com.sksamuel.kotest.engine.active.EnabledTestConfigFlagTest
import com.sksamuel.kotest.engine.active.FocusTest
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.matchers.shouldBe

@Isolate
@EnabledIf(LinuxOnlyGithubCondition::class)
class TestEngineTest : FunSpec({

   test("should sort specs using project config") {

      val c = object : AbstractProjectConfig() {
         override val specExecutionOrder = SpecExecutionOrder.Lexicographic
      }

      var sorted = emptyList<SpecRef>()

      var str = ""

      val listener = object : AbstractTestEngineListener() {
         override suspend fun specStarted(ref: SpecRef) {
            str += ref.fqn
         }
      }

      val result = TestEngineLauncher()
         .withListener(listener)
         .withProjectConfig(c)
         .withSpecRefs(
            listOf(
               SpecRef.Reference(EnabledTestConfigFlagTest::class),
               SpecRef.Reference(BangDisableFunSpec::class),
               SpecRef.Reference(FocusTest::class),
            )
         )
         .execute()

      str shouldBe "abc"
   }
})
