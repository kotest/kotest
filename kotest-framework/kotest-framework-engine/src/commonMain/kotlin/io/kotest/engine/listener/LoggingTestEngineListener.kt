package io.kotest.engine.listener

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.mpp.bestName
import io.kotest.mpp.log
import kotlin.reflect.KClass

object LoggingTestEngineListener : AbstractTestEngineListener() {

   override suspend fun engineFinished(t: List<Throwable>) {
      log { "LoggingTestEngineListener: Engine finished $t" }
   }

   override suspend fun specEnter(kclass: KClass<*>) {
      log { "LoggingTestEngineListener: specEnter '${kclass.bestName()}'" }
   }

   override suspend fun specExit(kclass: KClass<*>, t: Throwable?) {
      log { "LoggingTestEngineListener: specExit '${kclass.bestName()}' error=$t" }
   }

   override suspend fun testStarted(testCase: TestCase) {
      log { "LoggingTestEngineListener: testStarted '${testCase.descriptor.path().value}'" }
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      log { "LoggingTestEngineListener: testFinished '${testCase.descriptor.path().value}' result ${result.status}" }
   }
}
