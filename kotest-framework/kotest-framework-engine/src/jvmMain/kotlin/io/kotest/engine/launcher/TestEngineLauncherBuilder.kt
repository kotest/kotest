package io.kotest.engine.launcher

import io.kotest.core.spec.Spec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CompositeTestEngineListener
import io.kotest.engine.listener.TestEngineListener
import kotlin.reflect.KClass

/**
 * Creates a [TestEngineLauncher] to be used to launch the test engine.
 */
data class TestEngineLauncherBuilder(
   val listeners: List<TestEngineListener>,
   val logging: Boolean,
   val classes: List<KClass<out Spec>>,
) {

   companion object {
      fun builder() = TestEngineLauncherBuilder(emptyList(), true, emptyList())
   }

   fun addListener(listener: TestEngineListener): TestEngineLauncherBuilder {
      return copy(listeners = listeners + listener)
   }

   fun withClasses(classes: List<KClass<out Spec>>): TestEngineLauncherBuilder {
      return copy(classes = classes)
   }

   fun build(): TestEngineLauncher {
      require(listeners.isNotEmpty()) { "At least one listener must be provided" }
      require(classes.isNotEmpty()) { "At least one spec must be provided" }
      return TestEngineLauncher(CompositeTestEngineListener(listeners))
         .withClasses(classes)
   }
}
