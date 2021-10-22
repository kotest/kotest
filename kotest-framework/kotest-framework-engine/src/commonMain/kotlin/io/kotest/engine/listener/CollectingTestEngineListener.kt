package io.kotest.engine.listener

import io.kotest.common.concurrentHashMap
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

class CollectingTestEngineListener : AbstractTestEngineListener() {

   val specs = concurrentHashMap<KClass<*>, Throwable?>()
   val tests = concurrentHashMap<TestCase, TestResult>()
   var errors = false

   override suspend fun specExit(kclass: KClass<*>, t: Throwable?) {
      specs[kclass] = t
      if (t != null) errors = true
   }

   override suspend fun specInstantiationError(kclass: KClass<*>, t: Throwable) {
      specs[kclass] = t
      errors = true
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      tests[testCase] = TestResult.Ignored(reason)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      tests[testCase] = result
      if (result.isFailure || result.isError) errors = true
   }

   override suspend fun specAborted(kclass: KClass<*>, t: Throwable) {
      errors = true
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      if (t.isNotEmpty()) errors = true
   }
}
