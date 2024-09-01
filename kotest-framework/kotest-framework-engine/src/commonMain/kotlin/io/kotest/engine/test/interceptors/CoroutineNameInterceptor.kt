package io.kotest.engine.test.interceptors

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

/**
 * A [TestExecutionInterceptor] that sets the [CoroutineName] to the test name.
 */
object CoroutineNameInterceptor : TestExecutionInterceptor {
   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: suspend (TestCase, TestScope) -> TestResult
   ): TestResult {
      return withContext(CoroutineName("kotest-test-${testCase.name.testName}")) {
         test(testCase, scope)
      }
   }
}

suspend fun main() {
   println("1 $coroutineContext")
   withContext(CoroutineName("foo")) {
      val name = coroutineContext[CoroutineName]?.name
      println("2 $name $coroutineContext")
      coroutineScope {
         val name = coroutineContext[CoroutineName]?.name
         println("3 $name $coroutineContext")
         println("3 parent " + coroutineContext[Job]?.parent)
         async(CoroutineName("bar")) {
            val name = coroutineContext[CoroutineName]?.name
            println("4 $name $coroutineContext")
            println("4 parent " + coroutineContext[Job]?.parent)
         }.await()
      }
   }
}
