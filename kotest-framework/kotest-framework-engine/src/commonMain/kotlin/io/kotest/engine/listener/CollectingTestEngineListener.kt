package io.kotest.engine.listener

import io.kotest.common.concurrentHashMap
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

class CollectingTestEngineListener : AbstractTestEngineListener() {

   val specs = concurrentHashMap<KClass<*>, Throwable?>()
   val tests = concurrentHashMap<TestCase, TestResult>()
   val names = mutableListOf<String>()
   var errors = false

   fun result(descriptor: Descriptor.TestDescriptor): TestResult? = tests.mapKeys { it.key.descriptor }[descriptor]
   fun result(testname: String): TestResult? = tests.mapKeys { it.key.name.testName }[testname]

   override suspend fun specFinished(kclass: KClass<*>, t: Throwable?) {
      specs[kclass] = t
      if (t != null) errors = true
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      tests[testCase] = TestResult.Ignored(reason)
      names.add(testCase.name.testName)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      tests[testCase] = result
      if (result.isFailure || result.isError) errors = true
      names.add(testCase.name.testName)
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      if (t.isNotEmpty()) errors = true
   }
}
