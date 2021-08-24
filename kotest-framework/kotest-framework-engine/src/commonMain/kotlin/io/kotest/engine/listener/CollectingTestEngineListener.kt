package io.kotest.engine.listener

import io.kotest.common.concurrentHashMap
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

class CollectingTestEngineListener : TestEngineListener {

   val specs = concurrentHashMap<KClass<*>, Throwable?>()
   val tests = concurrentHashMap<TestCase, TestResult>()

   override suspend fun specFinished(kclass: KClass<*>, t: Throwable?, results: Map<TestCase, TestResult>) {
      specs[kclass] = t
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      tests[testCase] = TestResult.ignored(reason)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      tests[testCase] = result
   }
}
