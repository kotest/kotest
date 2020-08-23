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

   override fun engineStarted(classes: List<KClass<out Spec>>) {
      reporter.engineStarted(classes)
   }

   override fun engineFinished(t: List<Throwable>) {
      reporter.engineFinished(t)
   }

   override fun specStarted(kclass: KClass<out Spec>) {
      reporter.specStarted(kclass)
   }

   override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {
      reporter.specFinished(kclass, t, results)
   }

   override fun testStarted(testCase: TestCase) {
      reporter.testStarted(testCase)
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      reporter.testFinished(testCase, result)
   }

   override fun testIgnored(testCase: TestCase, reason: String?) {
      reporter.testIgnored(testCase)
   }
}
