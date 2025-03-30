package com.sksamuel.kotest.engine.interceptors

import io.kotest.assertions.fail
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldMatchEach
import io.kotest.matchers.shouldBe
import org.opentest4j.TestAbortedException

@EnabledIf(LinuxOnlyGithubCondition::class)
class AbortedExceptionTest : FreeSpec({
   "TestAbortedException is handled" {
      val collector = CollectingTestEngineListener()

      TestEngineLauncher(collector)
         .withClasses(DummySpec::class)
         .launch()

      collector.tests.toList().shouldMatchEach(
         {
            it.first.name.name shouldBe "Test should be marked as Ignored"
            it.second.isIgnored.shouldBeTrue()
         },
         {
            it.first.name.name shouldBe "Failure is not reclassified"
            it.second.isFailure.shouldBeTrue()
         },
         {
            it.first.name.name shouldBe "Successful test is not reclassified"
            it.second.isSuccess.shouldBeTrue()
         }
      )
   }
})

private class DummySpec : FreeSpec({
   "Test should be marked as Ignored" {
      throw TestAbortedException()
   }

   "Failure is not reclassified" {
      fail("should not be ignored")
   }

   "Successful test is not reclassified" {
   }
})

