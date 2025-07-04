package io.kotest.engine.test.interceptors

import io.kotest.core.Logger
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestScope
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.spec.interceptor.SpecContext

internal class DuplicateTestNameInterceptor(
   private val config: SpecConfigResolver,
   private val specContext: SpecContext
) :
   TestExecutionInterceptor {

   private val logger = Logger(DuplicateTestNameInterceptor::class)

   override suspend fun intercept(
      testCase: TestCase,
      scope: TestScope,
      test: NextTestExecutionInterceptor
   ): TestResult {

      val mode = config.duplicateTestNameMode(testCase.spec)
      val uniqueName = specContext.handler.handle(mode, testCase.name)

      logger.log { Pair(testCase.name.name, "Name with duplicate handling: $uniqueName") }
      return test.invoke(testCase.copy(name = testCase.name.copy(name = uniqueName)), scope)
   }
}
