//package io.kotest.engine.test.interceptors
//
//import io.kotest.core.test.TestCase
//import io.kotest.core.test.TestResult
//import io.kotest.core.test.TestScope
//import io.kotest.engine.test.scopes.withCoroutineContext
//import io.kotest.mpp.Logger
//import kotlinx.coroutines.coroutineScope
//
///**
// * Execute the test case wrapped in a [coroutineScope], so that we wait for any child coroutines launched
// * by the user inside the test function to complete before the engine marks the test as completed.
// */
//internal object CoroutineScopeInterceptor : TestExecutionInterceptor {
//
//   private val logger = Logger(CoroutineScopeInterceptor::class)
//
//   override suspend fun intercept(
//      testCase: TestCase,
//      scope: TestScope,
//      test: suspend (TestCase, TestScope) -> TestResult
//   ): TestResult {
//      logger.log { Pair(testCase.name.testName, "Creating coroutineScope") }
//      // this scope could throw if a user launches a coroutine inside the test, and the test then completes.
//      return
//   }
//}
