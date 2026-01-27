package com.sksamuel.kotest.engine

import io.kotest.common.KotestTesting
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

@OptIn(KotestTesting::class)
@EnabledIf(LinuxOnlyGithubCondition::class)
class ProjectTimeoutEngineTest : FunSpec({

   test("should return ProjectTimeoutException when project times out") {
      val c = object : AbstractProjectConfig() {
         override val projectTimeout = 1.milliseconds
      }

      val result = TestEngineLauncher()
         .withListener(NoopTestEngineListener)
         .withProjectConfig(c)
         .withSpecRefs(SpecRef.Reference(DummySpec4::class))
         .execute()

      result.errors.size shouldBe 1
//      result.errors.first().shouldBeInstanceOf<ProjectTimeoutException>()
   }

   test("should not return ProjectTimeoutException when project does not time out") {
      val c = object : AbstractProjectConfig() {
         override val projectTimeout = 100000.milliseconds
      }

      val result = TestEngineLauncher()
         .withListener(NoopTestEngineListener)
         .withProjectConfig(c)
         .withSpecRefs(SpecRef.Reference(DummySpec5::class))
         .execute()

      result.errors.size shouldBe 0
   }
})

private class DummySpec4 : FunSpec() {
   init {
      test("a") {
         delay(10.hours)
      }
   }
}

private class DummySpec5 : FunSpec() {
   init {
      test("a") {
      }
   }
}
