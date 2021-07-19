package com.sksamuel.kotest.engine

import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.engine.listener.TestEngineListener
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class CapturingTestListener : TestEngineListener {

   val specsFinished = ConcurrentHashMap<KClass<*>, Throwable?>()
   val testsFinished = ConcurrentHashMap<DescriptionName.TestName, TestStatus>()

   override fun specFinished(kclass: KClass<*>, t: Throwable?, results: Map<TestCase, TestResult>) {
      specsFinished[kclass] = t
   }

   override fun testIgnored(testCase: TestCase, reason: String?) {
      testsFinished[testCase.description.name] = TestStatus.Ignored
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      testsFinished[testCase.description.name] = result.status
   }
}
