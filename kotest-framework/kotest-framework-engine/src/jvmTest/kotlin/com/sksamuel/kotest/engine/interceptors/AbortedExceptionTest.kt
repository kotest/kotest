package com.sksamuel.kotest.engine.interceptors

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FreeSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldMatchEach
import io.kotest.matchers.shouldBe
import org.opentest4j.TestAbortedException
import org.opentest4j.TestSkippedException

@EnabledIf(LinuxOnlyGithubCondition::class)
class AbortedExceptionTest : FreeSpec({
   "opentest4j TestAbortedException is handled" {
      val collector = CollectingTestEngineListener()

      TestEngineLauncher().withListener(collector)
         .withClasses(DummySpec::class)
         .launch()

      collector.tests.toList().shouldMatchEach(
         {
            it.first.name.name shouldBe "opentest4j TestAbortedException should be marked as Ignored"
            it.second.isIgnored.shouldBeTrue()
         },
         {
            it.first.name.name shouldBe "opentest4j TestSkippedException should be marked as Ignored"
            it.second.isIgnored.shouldBeTrue()
         },
         {
            it.first.name.name shouldBe "kotest TestAbortedException should be marked as Ignored"
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

   "opentest4j TestAbortedException should be marked as Ignored" {
      throw TestAbortedException()
   }

   "opentest4j TestSkippedException should be marked as Ignored" {
      throw TestSkippedException()
   }

   "kotest TestAbortedException should be marked as Ignored" {
      throw io.kotest.engine.TestAbortedException()
   }

   "Failure is not reclassified" {
      AssertionErrorBuilder.fail("should not be ignored")
   }

   "Successful test is not reclassified" {
   }
})

