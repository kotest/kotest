@file:Suppress("UNCHECKED_CAST")

package io.kotest.engine.launcher

import io.kotest.core.Tags
import io.kotest.core.spec.Spec
import io.kotest.engine.KotestEngineLauncher
import io.kotest.engine.reporter.Reporter
import io.kotest.framework.discovery.Discovery
import io.kotest.framework.discovery.DiscoveryRequest
import io.kotest.framework.discovery.DiscoverySelector
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

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

   val specClass = specFQN?.let { (Class.forName(it) as Class<Spec>).kotlin }

   // if the spec class was null, then we perform discovery to locate all the classes
   // otherwise that specific spec class is used
   val specs = when (specClass) {
      null -> {
         val packageSelector = packageName?.let { DiscoverySelector.PackageDiscoverySelector(it) }
         val req = DiscoveryRequest(selectors = listOfNotNull(packageSelector))
         val result = Discovery(emptyList()).discover(req)
         result.specs
      }
      else -> listOf(specClass)
   }

   val filter = if (testPath == null || specClass == null) null else {
      TestPathTestCaseFilter(testPath, specClass)
   }

   try {
      future {
         KotestEngineLauncher()
            .withListener(ReporterTestEngineListener(reporter))
            .withSpecs(specs)
            .withTags(tags)
            .withFilters(listOfNotNull(filter))
            .withDumpConfig(true)
            .launch()
      }
   } catch (e: Throwable) {
   }
}

// this avoids us needing to bring in the coroutine deps, plus running inside the main
// thread is exactly what we want to do
fun future(f: suspend () -> Unit): Future<Unit> =
   CompletableFuture<Unit>().apply {
      f.startCoroutine(Continuation(EmptyCoroutineContext) { res ->
         res.fold(::complete, ::completeExceptionally)
      })
   }
