package io.kotest.engine.listener

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.spec.SpecExecutorDelegate
import io.kotest.core.Logger
import io.kotest.mpp.bestName
import kotlin.reflect.KClass

internal object LoggingTestEngineListener : AbstractTestEngineListener() {

   private val logger = Logger(SpecExecutorDelegate::class)

   override suspend fun engineFinished(t: List<Throwable>) {
      logger.log { Pair(null, "Engine finished $t") }
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      logger.log { Pair(kclass.bestName(), "specStarted") }
   }

   override suspend fun specFinished(kclass: KClass<*>, result: TestResult) {
      logger.log { Pair(kclass.bestName(), "specFinished") }
   }

   override suspend fun testStarted(testCase: TestCase) {
      logger.log { Pair(testCase.name.testName, "testStarted") }
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      logger.log { Pair(testCase.name.testName, "testFinished") }
   }
}
