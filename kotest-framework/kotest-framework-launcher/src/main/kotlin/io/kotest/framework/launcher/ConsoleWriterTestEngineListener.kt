package io.kotest.framework.launcher

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.framework.console.ConsoleWriter
import kotlin.reflect.KClass

/**
 * A [TestEngineListener] that adapts a [ConsoleWriter]
 */
class ConsoleWriterTestEngineListener(private val writer: ConsoleWriter) : TestEngineListener {

   override fun engineStarted(classes: List<KClass<out Spec>>) {
      writer.engineStarted(classes)
   }

   override fun engineFinished(t: List<Throwable>) {
      writer.engineFinished(t)
   }

   override fun specStarted(kclass: KClass<out Spec>) {
      writer.specStarted(kclass)
   }

   override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {
      writer.specFinished(kclass, t, results)
   }

   override fun testStarted(testCase: TestCase) {
      writer.testStarted(testCase)
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      writer.testFinished(testCase, result)
   }
}
