package io.kotest.runner.junit.platform

import io.kotest.engine.launcher.KotestEngineLauncher
import io.kotest.engine.instantiateSpec
import io.kotest.engine.spec.AbstractSpec
import io.kotest.fp.Try.Failure
import io.kotest.fp.Try.Success
import kotlinx.coroutines.runBlocking
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

class KotestTestRunner(
   private val klass: Class<out AbstractSpec>
) : Runner() {

   override fun run(notifier: RunNotifier) = runBlocking {
      val listener = JUnitTestEngineListener(notifier)
      KotestEngineLauncher(listener).forSpec(klass.kotlin).launch()
   }

   override fun getDescription(): Description = klass.let { klass ->
      instantiateSpec(klass.kotlin).let {
         when (it) {
            is Failure -> throw it.error
            is Success -> {
               val spec = it.value
               val desc = Description.createSuiteDescription(spec::class.java)
               spec.rootTests().forEach { rootTest -> desc.addChild(describeTestCase(rootTest.testCase)) }
               desc
            }
         }
      }
   }
}
