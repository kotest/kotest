@file:Suppress("UNCHECKED_CAST")

package io.kotest.runner.console

import io.kotest.core.Tags
import io.kotest.core.engine.KotestEngineLauncher
import io.kotest.core.engine.TestEngineListener
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Creates a kotest engine and launches the tests.
 */
class KotestConsoleRunner(private val listener: TestEngineListener) {

   suspend fun execute(packageName: String?, specFQN: String?, testPath: String?, tags: Tags?) {
      val launcher = KotestEngineLauncher(listener).withTags(tags)
      val spec = specFQN?.let { Class.forName(it).kotlin as KClass<out Spec> }
      when {
         spec != null && testPath != null ->
            launcher.forSpec(spec).addFilter(TestPathTestCaseFilter(testPath, spec)).launch()
         spec != null -> launcher.forSpec(spec).launch()
         packageName != null -> launcher.forPackage(packageName).launch()
         else -> launcher.launch()
      }
   }
}
