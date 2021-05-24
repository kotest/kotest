package io.kotest.runner.junit4

import io.kotest.core.spec.Spec
import io.kotest.core.spec.materializeAndOrderRootTests
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.spec.createAndInitializeSpec
import io.kotest.fp.Try.Failure
import io.kotest.fp.Try.Success
import kotlinx.coroutines.runBlocking
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

class KotestTestRunner(
   private val klass: Class<out Spec>
) : Runner() {

   override fun run(notifier: RunNotifier) {
      runBlocking {
         val listener = JUnitTestEngineListener(notifier)
         KotestEngineLauncher
            .default(listOf(listener), listOf(klass.kotlin), null)
            .launch()
      }
   }

   override fun getDescription(): Description = klass.let { klass ->
      createAndInitializeSpec(klass.kotlin).let {
         when (it) {
            is Failure -> throw it.error
            is Success -> {
               val spec = it.value
               val desc = Description.createSuiteDescription(spec::class.java)
               spec.materializeAndOrderRootTests().forEach { rootTest -> desc.addChild(describeTestCase(rootTest.testCase)) }
               desc
            }
         }
      }
   }
}
