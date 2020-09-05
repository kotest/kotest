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
import io.kotest.framework.discovery.Discovery
import io.kotest.framework.discovery.DiscoveryRequest
import io.kotest.framework.discovery.DiscoverySelector
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
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
) {
   future {
      try {

         val specClass = specFQN?.let { (Class.forName(it) as Class<Spec>).kotlin }
         val specs = specs(specClass, packageName)
         val filter = if (testPath == null || specClass == null) null else {
            TestPathTestCaseFilter(testPath, specClass)
         }

         KotestEngineLauncher()
            .withListener(ReporterTestEngineListener(reporter))
            .withSpecs(specs)
            .withTags(tags)
            .withFilters(listOfNotNull(filter))
            .withDumpConfig(true)
            .launch()

      } catch (e: Throwable) {
         println(e)
         e.printStackTrace()
         reporter.engineFinished(listOf(e))
      }
   }
}

/**
 * Returns the spec classes to execute by using an FQN class name, a package scan,
 * or a full scan.
 */
private fun specs(specClass: KClass<out Spec>?, packageName: String?): List<KClass<out Spec>> {
   // if the spec class was null, then we perform discovery to locate all the classes
   // otherwise that specific spec class is used
   return when (specClass) {
      null -> scan(packageName)
      else -> listOf(specClass)
   }
}

private fun scan(packageName: String?): List<KClass<out Spec>> {
   val packageSelector = packageName?.let { DiscoverySelector.PackageDiscoverySelector(it) }
   val req = DiscoveryRequest(selectors = listOfNotNull(packageSelector))
   val extensions = listOf(
      IgnoredSpecDiscoveryExtension,
      EnabledConditionSpecDiscoveryExtension,
      EnabledConditionSpecDiscoveryExtension,
      TagsExcludedDiscoveryExtension,
   ) + configuration.extensions().filterIsInstance<DiscoveryExtension>()
   val result = Discovery(extensions).discover(req)
   return result.specs
}

// this avoids us needing to bring in the coroutine deps, plus running inside the main
// thread is exactly what we want to do
private fun future(f: suspend () -> Unit): CompletableFuture<Unit> =
   CompletableFuture<Unit>().apply {
      f.startCoroutine(Continuation(EmptyCoroutineContext) { res ->
         res.fold(::complete, ::completeExceptionally)
      })
   }
