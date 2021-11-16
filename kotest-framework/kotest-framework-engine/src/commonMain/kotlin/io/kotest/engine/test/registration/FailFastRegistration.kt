package io.kotest.engine.test.registration

import io.kotest.core.config.Configuration
import io.kotest.core.spec.Registration
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.Materializer
import io.kotest.mpp.Logger

class FailFastRegistration(
   private val testCase: TestCase,
   configuration: Configuration,
   private val listener: TestEngineListener,
   private val delegate: Registration,
) : Registration {

   private val logger = Logger(this::class)

   // set to true once this registration has a failed test
   private var containsFailedTest = false

   private val materializer = Materializer(configuration)

   override suspend fun registerNestedTest(nested: NestedTest): TestResult? {
      return if (containsFailedTest && testCase.config.failfast) {
         logger.log { Pair(testCase.name.testName, "Skipping test due to fail fast") }
         listener.testIgnored(materializer.materialize(nested, testCase), "Failfast enabled on parent test")
         TestResult.Ignored("Skipping test due to fail fast")
      } else {
         val result = delegate.registerNestedTest(nested)
         if (result != null && result.isErrorOrFailure) containsFailedTest = true
         result
      }
   }

}
