package io.kotest.runner.junit4

import io.kotest.engine.extensions.EmptyExtensionRegistry
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.Spec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.spec.Materializer
import io.kotest.engine.spec.SpecInstantiator
import io.kotest.engine.test.names.DefaultDisplayNameFormatter
import kotlinx.coroutines.runBlocking
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

class KotestTestRunner(
   private val kclass: Class<out Spec>
) : Runner() {

   private val formatter = DefaultDisplayNameFormatter(ProjectConfiguration())

   override fun run(notifier: RunNotifier) {
      runBlocking {
         val listener = JUnitTestEngineListener(notifier)
         TestEngineLauncher(listener).withClasses(kclass.kotlin).launch()
      }
   }

   override fun getDescription(): Description {
      val spec = runBlocking { SpecInstantiator(EmptyExtensionRegistry).createAndInitializeSpec(kclass.kotlin).getOrThrow() }
      val desc = Description.createSuiteDescription(spec::class.java)
      Materializer(ProjectConfiguration()).materialize(spec).forEach { rootTest ->
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
