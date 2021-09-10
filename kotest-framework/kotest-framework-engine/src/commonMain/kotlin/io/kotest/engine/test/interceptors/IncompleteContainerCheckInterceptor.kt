package io.kotest.engine.test.interceptors

import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import kotlin.coroutines.CoroutineContext

/**
 * Tests that a container test contains at least one test.
 */
object IncompleteContainerCheckInterceptor : TestExecutionInterceptor {

   override suspend fun intercept(
      test: suspend (TestCase, TestContext) -> TestResult
   ): suspend (TestCase, TestContext) -> TestResult = { testCase, context ->

      when (testCase.type) {
         TestType.Container -> {
            val ctc = CollectingTestContext(context)
            test(testCase, ctc).apply {
               if (ctc.count == 0)
                  throw IncompleteContainerTestException(testCase)
            }
         }
         TestType.Test -> test(testCase, context)
      }
   }
}

class CollectingTestContext(private val delegate: TestContext) : TestContext {

   var count = 0

   override val testCase: TestCase = delegate.testCase
   override val coroutineContext: CoroutineContext = delegate.coroutineContext

   override suspend fun registerTestCase(nested: NestedTest) {
      delegate.registerTestCase(nested)
      count++
   }
}

class IncompleteContainerTestException(testCase: TestCase) :
   RuntimeException("Test ${testCase.displayName} did not contain any nested tests. This is invalid for a container test")
