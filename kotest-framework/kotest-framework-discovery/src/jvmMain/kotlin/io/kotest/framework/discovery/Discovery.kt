package io.kotest.framework.discovery

import io.github.classgraph.ClassGraph
import io.kotest.core.log
import io.kotest.core.spec.Spec
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.time.measureTimedValue

/**
 * Contains the results of a discovery scan.
 *
 * @param specs these are classes which extend one of the spec classes
 * @param error if an error occurred during discovery
 */
data class DiscoveryResult(
   val specs: List<KClass<out Spec>>,
   val error: Throwable?, // this error is set if there was an exception during discovery
) {
   companion object {
      fun error(t: Throwable): DiscoveryResult = DiscoveryResult(emptyList(), t)
   }
}

/**
 * Scans for tests as specified by a [DiscoveryRequest].
 */
class Discovery(private val classgraph: ClassGraph) {

   private val requests = ConcurrentHashMap<DiscoveryRequest, DiscoveryResult>()

   // filter functions
   private val isSpecSubclassKt: (KClass<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it.java) }
   private val isSpecSubclass: (Class<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it) }
   private val isAbstract: (KClass<*>) -> Boolean = { it.isAbstract }
   private val cachedSpecsFromClassPaths: List<KClass<out Spec>> by lazy { specsFromClassGraph() }

   /**
    * Returns a function that applies all the [DiscoveryFilter]s to a given class.
    * The class must pass all the filters to be included.
    */
   private fun filterFn(filters: List<DiscoveryFilter>): (KClass<out Spec>) -> Boolean = { kclass ->
      filters.isEmpty() || filters.all { it.test(kclass) }
   }

   /**
    * Returns a function that applies all the [DiscoverySelector]s to a given class.
    * The class must pass any one selector to be included.
    */
   private fun selectorFn(selectors: List<DiscoverySelector>): (KClass<out Spec>) -> Boolean = { kclass ->
      selectors.isEmpty() || selectors.any { it.test(kclass) }
   }

   fun discover(request: DiscoveryRequest): DiscoveryResult =
      requests.getOrPut(request) { doDiscovery(request).getOrElse { DiscoveryResult.error(it) } }

   private fun doDiscovery(request: DiscoveryRequest): Result<DiscoveryResult> = runCatching {

      log { "[Discovery] Starting spec discovery" }

      val specsSelected = request.specsFromClassDiscoverySelectorsOnlyOrNull()
         ?: cachedSpecsFromClassPaths
            .asSequence()
            .filter(isSpecSubclassKt)
            .filterNot(isAbstract)
            .filter(selectorFn(request.selectors))
            .toList()

      val specsAfterInitialFiltering = specsSelected.filter(filterFn(request.filters))

      log { "[Discovery] ${specsAfterInitialFiltering.size} specs remain after initial filtering" }

      DiscoveryResult(specsAfterInitialFiltering, null)
   }

   /**
    * Returns the request's [Spec]s if they are completely specified by class selectors, null otherwise.
    */
   private fun DiscoveryRequest.specsFromClassDiscoverySelectorsOnlyOrNull(): List<KClass<out Spec>>? {
      if (selectors.isEmpty() || !selectors.all { it is DiscoverySelector.ClassDiscoverySelector })
         return null

      val (specs, duration) = measureTimedValue {
         // first filter down to spec instances only, then load the full class
         selectors
            .asSequence()
            .filterIsInstance<DiscoverySelector.ClassDiscoverySelector>()
            .map { Class.forName(it.className, false, this::class.java.classLoader) }
            .filter(isSpecSubclass)
            .map { Class.forName(it.name).kotlin }
            .filterIsInstance<KClass<out Spec>>()
            .filterNot(isAbstract)
            .toList()
      }

      log {
         "[Discovery] Collected specs via ${selectors.size} class discovery selectors in ${duration}," +
            " found ${specs.size} specs"
      }

      return specs
   }

   /**
    * Returns a list of [Spec] classes detected using classgraph in the list of
    * locations specified by the uris param.
    */
   private fun specsFromClassGraph(): List<KClass<out Spec>> {
      val (specs, duration) = measureTimedValue {
         classgraph.scan().use { scanResult ->
            scanResult
               .getSubclasses(Spec::class.java.name)
               .map { Class.forName(it.name).kotlin }
               .filterIsInstance<KClass<out Spec>>()
         }
      }

      log {
         "[Discovery] Scanned classgraph for specs in ${duration}, found ${specs.size} specs"
      }

      return specs
   }
}
