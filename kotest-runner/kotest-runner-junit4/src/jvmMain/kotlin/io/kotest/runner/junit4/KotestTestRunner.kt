package io.kotest.runner.junit4

import io.kotest.core.config.Configuration
import io.kotest.core.config.EmptyExtensionRegistry
import io.kotest.core.spec.Spec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.createAndInitializeSpec
import io.kotest.engine.test.names.DefaultDisplayNameFormatter
import kotlinx.coroutines.runBlocking
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

class KotestTestRunner(
   private val kclass: Class<out Spec>
) : Runner() {

   private val formatter = DefaultDisplayNameFormatter(Configuration())

   override fun run(notifier: RunNotifier) {
      runBlocking {
         val listener = JUnitTestEngineListener(notifier)
         TestEngineLauncher(listener).withClasses(kclass.kotlin).launch()
      }
   }

   override fun getDescription(): Description {
      val spec = runBlocking { createAndInitializeSpec(kclass.kotlin, EmptyExtensionRegistry).getOrThrow() }
      val desc = Description.createSuiteDescription(spec::class.java)
      Materializer(Configuration()).materialize(spec).forEach { rootTest ->
         desc.addChild(
            describeTestCase(
               rootTest,
               formatter.format(rootTest)
            )
         )
      }
      return desc
   }
}
