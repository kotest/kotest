package io.kotest.engine.test.registration

import io.kotest.core.config.Configuration
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.spec.Registration
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.Materializer
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.mpp.Logger

/**
 * A [Registration] that will ignore any nested test cases if another test
 * case has already failed.
 */
class FailFastRegistration(
   private val listener: TestCaseExecutionListener,
   private val configuration: Configuration,
   private val delegate: Registration,
) : Registration {

   private val logger = Logger(this::class)
   private val failures = mutableSetOf<Descriptor.TestDescriptor>()

   override suspend fun runNestedTestCase(parent: TestCase, nested: NestedTest): TestResult? {
      // if we have already failed and failfast is enabled, we ignore any other registration attempts
      return if (parent.config.failfast && failures.contains(parent.descriptor)) {
         val nestedTestCase = Materializer(configuration).materialize(nested, parent)
         logger.log { Pair(parent.name.testName, "Failfast enabled - will ignore this nested test") }
         val result = TestResult.Ignored("Failfast enabled on parent test")
         listener.testIgnored(nestedTestCase, result)
         result
      } else {
         delegate.runNestedTestCase(parent, nested)?.apply {
            if (isErrorOrFailure) failures.add(parent.descriptor)
         }
      }
   }
}
