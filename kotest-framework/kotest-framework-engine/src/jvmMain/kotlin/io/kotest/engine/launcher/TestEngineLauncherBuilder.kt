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

//@Suppress("UNCHECKED_CAST")
//internal fun setupLauncher(
//   args: LauncherArgs,
//   listener: TestEngineListener,
//): Result<TestEngineLauncher> = runCatching {
//
//   val specClass = args.spec?.let { (Class.forName(it) as Class<Spec>).kotlin }
//
//   // todo move discovery out of this module entirely
////   val (specs, _, error) = specs(specClass, args.packageName)
//   val specs = emptyList<KClass<out Spec>>()
//   val error: Throwable? = null
//   val filter = if (args.testpath == null || specClass == null) null else {
//      TestPathTestCaseFilter(args.testpath, specClass)
//   }
//
//   if (error != null) throw error
//
//   TestEngineLauncher(listener)
//      .withExtensions(listOfNotNull(filter))
//      .withTagExpression(args.tagExpression?.let { TagExpression(it) })
//      .withClasses(specs)
//}

///**
// * Returns the spec classes to execute by using an FQN class name, a package scan,
