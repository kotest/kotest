package io.kotest.runner.jvm

import io.kotest.core.Description
import io.kotest.core.TestCase
import io.kotest.core.TestResult
import io.kotest.core.specs.Spec
import io.kotest.core.specs.SpecContainer
import io.kotest.core.specs.description
import java.util.concurrent.atomic.AtomicReference

/**
 * Gradle gets confused if test events are published concurrently.
 * So we must ensure that each spec is isolated in its output.
 */
@Suppress("LocalVariableName")
class IsolationTestEngineListener(val listener: TestEngineListener) : TestEngineListener {

   private val runningSpec = AtomicReference<Description?>(null)
   private val callbacks = mutableListOf<() -> Unit>()

   private fun queue(fn: () -> Unit) {
      callbacks.add { fn() }
   }

   private fun replay() {
      val _callbacks = callbacks.toList()
      callbacks.clear()
      _callbacks.forEach { it.invoke() }
   }

   private fun runOrQueue(description: Description, f: () -> Unit) {
      println("${runningSpec.get()} == $description")
      if (runningSpec.compareAndSet(null, description)) {
         f()
      } else {
         queue(f)
      }
   }

   override fun engineFinished(t: Throwable?) {
      listener.engineFinished(t)
   }

   override fun engineStarted(containers: List<SpecContainer>) {
      listener.engineStarted(containers)
   }

   override fun beginSpec(spec: Spec) {
      println("beginSpec ${spec.description()} current=${runningSpec.get()}")
      if (runningSpec.compareAndSet(null, spec.description())) {
         println("Runing has been updated to ${runningSpec.get()}")
         listener.beginSpec(spec)
         println("begin done")
      } else {
         queue {
            beginSpec(spec)
         }
      }
   }

   override fun specExecutionError(containers: SpecContainer, t: Throwable) {
      listener.specExecutionError(containers, t)
   }

   private fun isRunning(spec: Spec): Boolean {
      val running = runningSpec.get()
      val given = spec.description()
      return running == given
   }

   override fun enterTestCase(testCase: TestCase) {
      runOrQueue(testCase.spec.description()) {
         listener.enterTestCase(testCase)
      }
   }

   override fun invokingTestCase(testCase: TestCase, k: Int) {
      runOrQueue(testCase.spec.description()) {
         listener.invokingTestCase(testCase, k)
      }
   }

   override fun afterTestCaseExecution(testCase: TestCase, result: TestResult) {
      runOrQueue(testCase.spec.description()) {
         listener.afterTestCaseExecution(testCase, result)
      }
   }

   override fun exitTestCase(testCase: TestCase, result: TestResult) {
      runOrQueue(testCase.spec.description()) {
         listener.exitTestCase(testCase, result)
      }
   }

   override fun endSpec(spec: Spec, t: Throwable?) {
      println("Ending spec ${spec.description()} current=${runningSpec.get()}")
      runOrQueue(spec.description()) {
         println("Ending spec 2")
         listener.endSpec(spec, t)
         runningSpec.set(null)
         replay()
      }
   }
}
