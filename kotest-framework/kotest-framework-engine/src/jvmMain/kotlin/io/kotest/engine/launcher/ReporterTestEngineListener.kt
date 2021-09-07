package io.kotest.engine.launcher

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.reporter.Reporter
import kotlin.reflect.KClass

/**
 * A [TestEngineListener] that adapts a [Reporter]
 */
class ReporterTestEngineListener(private val reporter: Reporter) : TestEngineListener {

   override suspend fun engineStarted(classes: List<KClass<*>>) {
      reporter.engineStarted(classes)
   }

   override suspend fun engineFinished(t: List<Throwable>) {
      reporter.engineFinished(t)
   }

   override suspend fun specStarted(kclass: KClass<*>) {
      reporter.specStarted(kclass)
   }

   override suspend fun specFinished(kclass: KClass<*>, results: Map<TestCase, TestResult>) {
      reporter.specFinished(kclass, null, results)
   }

   override suspend fun testStarted(testCase: TestCase) {
      reporter.testStarted(testCase)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      reporter.testFinished(testCase, result)
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      reporter.testIgnored(testCase)
   }

   override suspend fun specExit(kclass: KClass<out Spec>, t: Throwable?) {
      reporter.specFinished(kclass, t, emptyMap())
   }
}
