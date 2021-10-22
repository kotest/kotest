package io.kotest.runner.junit4

import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.spec.createAndInitializeSpec
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.test.names.getDisplayNameFormatter
import kotlinx.coroutines.runBlocking
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

class KotestTestRunner(
   private val klass: Class<out Spec>
) : Runner() {

   private val formatter = getDisplayNameFormatter(configuration)

   override fun run(notifier: RunNotifier) {
      runBlocking {
         val listener = JUnitTestEngineListener(notifier)
         KotestEngineLauncher
            .default(listOf(listener), listOf(klass.kotlin), null)
            .launch()
      }
   }

   override fun getDescription(): Description {
      val spec = runBlocking { createAndInitializeSpec(klass.kotlin).getOrThrow() }
      val desc = Description.createSuiteDescription(spec::class.java)
      spec.materializeAndOrderRootTests()
         .forEach { rootTest ->
            desc.addChild(
               describeTestCase(
                  rootTest.testCase,
                  formatter.format(rootTest.testCase)
               )
            )
         }
      return desc
   }
}
