package io.kotest.runner.junit4

import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.engine.TestEngineLauncher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier

class KotestTestRunner(
   private val clazz: Class<out Spec>
) : Runner() {

   @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
   override fun run(notifier: RunNotifier) {
      runBlocking {
         val listener = JUnitTestEngineListener(notifier)
         TestEngineLauncher()
            .withListener(listener)
            .withSpecRefs(SpecRef.Reference(clazz.kotlin, clazz.name))
            .execute()
      }
   }

   override fun getDescription(): Description {
      return Description.createSuiteDescription(clazz)
   }
}
