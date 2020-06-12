@file:Suppress("UNCHECKED_CAST")

package io.kotest.runner.console

import io.kotest.core.Tags
import io.kotest.core.config.Project
import io.kotest.core.engine.KotestEngine
import io.kotest.core.engine.TestEngineListener
import io.kotest.core.engine.discovery.Discovery
import io.kotest.core.engine.discovery.DiscoveryRequest
import io.kotest.core.engine.discovery.DiscoverySelector
import io.kotest.core.spec.Spec

class KotestConsoleRunner(private val listener: TestEngineListener) {

   suspend fun execute(packageName: String?, specFQN: String?, testPath: String?, tags: Tags?) {

      // if the spec class was null, then we perform discovery to locate all the classes
      // otherwise we instantiate that particular spec
      val (specs, filter) = if (specFQN == null) {
         val packageSelector = packageName?.let { DiscoverySelector.PackageDiscoverySelector(it) }
         val result = Discovery.discover(DiscoveryRequest(selectors = listOfNotNull(packageSelector)))
         Pair(result.specs, null)
      } else {
         val spec = (Class.forName(specFQN) as Class<Spec>).kotlin
         val filter = testPath?.let { TestPathTestCaseFilter(it, spec) }
         listOf(spec) to filter
      }

      val runner = KotestEngine(
         specs,
         listOfNotNull(filter),
         Project.parallelism(),
         tags,
         listener
      )
      runner.execute()
   }
}
