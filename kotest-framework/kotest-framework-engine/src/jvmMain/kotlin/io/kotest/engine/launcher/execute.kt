@file:Suppress("UNCHECKED_CAST")

package io.kotest.engine.launcher

import io.kotest.core.Tags
import io.kotest.core.config.configuration
import io.kotest.core.extensions.DiscoveryExtension
import io.kotest.core.spec.Spec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.extensions.EnabledConditionSpecDiscoveryExtension
import io.kotest.engine.extensions.IgnoredSpecDiscoveryExtension
import io.kotest.engine.extensions.TagsExcludedDiscoveryExtension
import io.kotest.engine.reporter.Reporter
import io.kotest.fp.Try
import io.kotest.framework.discovery.Discovery
import io.kotest.framework.discovery.DiscoveryRequest
import io.kotest.framework.discovery.DiscoveryResult
import io.kotest.framework.discovery.DiscoverySelector
import kotlin.reflect.KClass

/**
 * Creates a kotest engine and launches the tests.
 */
fun execute(
   reporter: Reporter,
   packageName: String?,
   specFQN: String?,
   testPath: String?,
   tags: Tags?,
   dumpconfig: Boolean = true,
) {
   setupLauncher(specFQN, packageName, testPath, tags, dumpconfig, reporter)
      .fold(
         {
            reporter.engineStarted(emptyList())
            reporter.engineFinished(listOf(it))
         },
         { launcher ->
            executeLauncher(launcher)
               .onFailure { reporter.engineFinished(listOf(it)) }
         }
      )
}

private fun executeLauncher(launcher: KotestEngineLauncher) = Try {
   launcher.launch()
}

private fun setupLauncher(
   specFQN: String?,
   packageName: String?,
   testPath: String?,
   tags: Tags?,
   dumpconfig: Boolean = true,
   reporter: Reporter
): Try<KotestEngineLauncher> = Try {

   val specClass = specFQN?.let { (Class.forName(it) as Class<Spec>).kotlin }
   val (specs, _, error) = specs(specClass, packageName)
   val filter = if (testPath == null || specClass == null) null else {
      TestPathTestCaseFilter(testPath, specClass)
   }

   if (error != null) throw error

   KotestEngineLauncher()
      .withListener(ReporterTestEngineListener(reporter))
      .withSpecs(specs)
      .withTags(tags)
      .withFilters(listOfNotNull(filter))
      .withDumpConfig(dumpconfig)
}

/**
 * Returns the spec classes to execute by using an FQN class name, a package scan,
 * or a full scan.
 */
private fun specs(specClass: KClass<out Spec>?, packageName: String?): DiscoveryResult {
   // if the spec class was null, then we perform discovery to locate all the classes
   // otherwise that specific spec class is used
   return when (specClass) {
      null -> scan(packageName)
      else -> DiscoveryResult(listOf(specClass), emptyList(), null)
   }
}

private fun scan(packageName: String?): DiscoveryResult {
   val packageSelector = packageName?.let { DiscoverySelector.PackageDiscoverySelector(it) }
   val req = DiscoveryRequest(selectors = listOfNotNull(packageSelector))
   val extensions = listOf(
      IgnoredSpecDiscoveryExtension,
      EnabledConditionSpecDiscoveryExtension,
      EnabledConditionSpecDiscoveryExtension,
      TagsExcludedDiscoveryExtension,
   ) + configuration.extensions().filterIsInstance<DiscoveryExtension>()
   val discovery = Discovery(extensions)
   val result = discovery.discover(req)
   discovery.close()
   return result
}
