package com.sksamuel.kotest.engine.interceptors

import io.kotest.assertions.fail
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.names.TestName
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.interceptors.MarkAbortedExceptionsAsSkippedTestInterceptor
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.inspectors.forOne
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldMatchEach
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.maps.shouldMatchAll
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.opentest4j.TestAbortedException
import kotlin.time.Duration.Companion.milliseconds

class AbortedExceptionTest : FreeSpec({
   "TestAbortedException is handled" {
      val collector = CollectingTestEngineListener()

      TestEngineLauncher(collector)
         .withClasses(DummySpec::class)
         .launch()

      collector.tests.toList().shouldMatchEach(
         {
            it.first.name.testName shouldBe "Test should be marked as Ignored"
            it.second.isIgnored.shouldBeTrue()
         },
         {
            it.first.name.testName shouldBe "Failure is not reclassified"
            it.second.isFailure.shouldBeTrue()
         },
         {
            it.first.name.testName shouldBe "Successful test is not reclassified"
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

