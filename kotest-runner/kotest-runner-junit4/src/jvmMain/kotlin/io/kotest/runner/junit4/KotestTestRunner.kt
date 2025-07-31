package io.kotest.runner.junit4

import io.kotest.core.spec.Spec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.config.ProjectConfigResolver
import io.kotest.engine.config.SpecConfigResolver
import io.kotest.engine.config.TestConfigResolver
import io.kotest.engine.extensions.DefaultExtensionRegistry
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

   private val formatter = DefaultDisplayNameFormatter(ProjectConfigResolver(), TestConfigResolver())

   override fun run(notifier: RunNotifier) {
      runBlocking {
         val listener = JUnitTestEngineListener(notifier)
         TestEngineLauncher().withListener(listener).withClasses(kclass.kotlin).launch()
      }
   }

   override fun getDescription(): Description {
      val spec = runBlocking {
         SpecInstantiator(
            DefaultExtensionRegistry(),
            ProjectConfigResolver()
         ).createAndInitializeSpec(kclass.kotlin).getOrThrow()
      }
      val desc = Description.createSuiteDescription(spec::class.java)
      Materializer(SpecConfigResolver()).materialize(spec).forEach { rootTest ->
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
