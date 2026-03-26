@file:Suppress("DEPRECATION")

package io.kotest.engine.listener

import io.kotest.common.reflection.bestName
import io.kotest.core.LogLine
import io.kotest.core.Logger
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult

object LoggingTestEngineListener : AbstractTestEngineListener() {

   private val logger = Logger<LoggingTestEngineListener>()

   override suspend fun engineFinished(t: List<Throwable>) {
      logger.log { "Engine finished $t" }
   }

   override suspend fun specStarted(ref: SpecRef) {
      logger.log { LogLine(ref.kclass.bestName(), "specStarted") }
   }

   override suspend fun specFinished(ref: SpecRef, result: TestResult) {
      logger.log { LogLine(ref.kclass.bestName(), "specFinished") }
   }

   override suspend fun testStarted(testCase: TestCase) {
      logger.log { LogLine(testCase.name.name, "testStarted") }
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      logger.log { LogLine(testCase.name.name, "testFinished") }
   }
}
