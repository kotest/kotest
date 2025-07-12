@file:Suppress("DEPRECATION")

package io.kotest.engine.listener

import io.kotest.core.Logger
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.mpp.bestName

object LoggingTestEngineListener : AbstractTestEngineListener() {

   private val logger = Logger(LoggingTestEngineListener::class)

   override suspend fun engineFinished(t: List<Throwable>) {
      logger.log { Pair(null, "Engine finished $t") }
   }

   override suspend fun specStarted(ref: SpecRef) {
      logger.log { Pair(ref.kclass.bestName(), "specStarted") }
   }

   override suspend fun specFinished(ref: SpecRef, result: TestResult) {
      logger.log { Pair(ref.kclass.bestName(), "specFinished") }
   }

   override suspend fun testStarted(testCase: TestCase) {
      logger.log { Pair(testCase.name.name, "testStarted") }
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      logger.log { Pair(testCase.name.name, "testFinished") }
   }
}
