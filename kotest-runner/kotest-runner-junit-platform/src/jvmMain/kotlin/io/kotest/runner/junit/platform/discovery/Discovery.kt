package io.kotest.runner.junit.platform.discovery

import io.kotest.core.log
import io.kotest.core.spec.Spec
import io.kotest.runner.junit.platform.Segment
import org.junit.platform.engine.ConfigurationParameters
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.engine.discovery.UniqueIdSelector
import kotlin.reflect.KClass

/**
 * Contains the results of a discovery scan.
 *
 * @param specs these are classes which extend one of the spec classes
 */
data class DiscoveryResult(
   val specs: List<KClass<out Spec>>,
)

/**
 * Converts JUnit selectors into Kotest specs.
 */
object Discovery {

   // filter functions
   private val isSpecSubclassKt: (KClass<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it.java) }
   private val isSpecSubclass: (Class<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it) }
   private val isAbstract: (KClass<*>) -> Boolean = { it.isAbstract }

   fun discover(engineId: UniqueId, request: EngineDiscoveryRequest): DiscoveryResult {

      log { "[Discovery] Starting spec discovery" }

      // kotest only supports class selectors and unique id selectors (which we convert to class selectors)
      val classSelectors = request.getSelectorsByType(ClassSelector::class.java) +
         convertUniqueIdsToClassSelectors(engineId, request)

      val specsSelected = specsFromClassDiscoverySelectorsOnly(classSelectors)
         .asSequence()
         .filter(isSpecSubclassKt)
         .filterNot(isAbstract)
         .toList()

      val specsAfterInitialFiltering = specsSelected.filter(filterFn(filters(request.configurationParameters)))

      log { "[Discovery] ${specsAfterInitialFiltering.size} specs will be returned" }

      return DiscoveryResult(specsAfterInitialFiltering)
   }

   private fun filters(configurationParameters: ConfigurationParameters): List<DiscoveryFilter> {
      val private = if (configurationParameters.get("allow_private").isPresent) Modifier.Private else null
      val modifiers = listOfNotNull(Modifier.Public, Modifier.Internal, private)
      return listOf(DiscoveryFilter.ClassModifierDiscoveryFilter(modifiers.toSet()))
   }

   /**
    * Returns the request's [Spec]s if they are completely specified by class selectors, null otherwise.
    */
   private fun specsFromClassDiscoverySelectorsOnly(selectors: List<ClassSelector>): List<KClass<out Spec>> {

      // first filter down to spec instances only, then load the full class
      val specs = selectors
         .asSequence()
         // must load the class without initializing it, we just want to check if it's a subclass of Spec
         .mapNotNull { runCatching { Class.forName(it.className, false, this::class.java.classLoader) }.getOrNull() }
         .filter(isSpecSubclass)
         // now we can properly initialize it
         .map { Class.forName(it.name).kotlin }
         .filterNot(isAbstract)
         // we know this will be true
         .filterIsInstance<KClass<out Spec>>()
         .toList()

      log {
         "[Discovery] Collected specs via ${selectors.size} class discovery selectors: found ${specs.size} specs"
      }

      return specs
   }

   /**
    * Returns a function that applies all the [DiscoveryFilter]s to a given class.
    * The class must pass all the filters to be included.
    */
   private fun filterFn(filters: List<DiscoveryFilter>): (KClass<out Spec>) -> Boolean = { kclass ->
      filters.isEmpty() || filters.all { it.test(kclass) }
   }

//   val classFilters = getFiltersByType(ClassNameFilter::class.java).map { filter ->
//      DiscoveryFilter.ClassNameDiscoveryFilter { filter.toPredicate().test(it.value) }
//   }
//
//   val packageFilters = getFiltersByType(PackageNameFilter::class.java).map { filter ->
//      DiscoveryFilter.PackageNameDiscoveryFilter { filter.toPredicate().test(it.value) }
//   }

   /**
    * Based on a previously discovered `TestPlan`, tools may decide to split
    * it and use `UniqueIdSelectors` to create multiple smaller `TestPlans`.
    * Another use case is rerunning a test class, for example, because it failed.
    *
    * This method converts these [UniqueIdSelector]s to [ClassSelector]s which we can then
    * use to extract the specs to run.
    */
   fun convertUniqueIdsToClassSelectors(engineId: UniqueId, request: EngineDiscoveryRequest): List<ClassSelector> {
      val engineIdLength = engineId.segments.size
      return request.getSelectorsByType(UniqueIdSelector::class.java)
         .asSequence()
         .map { it.uniqueId }
         .filter { it.hasPrefix(engineId) }
         .map { it.segments }
         .filter { it.size > engineIdLength }
         .map { it.asSequence().drop(engineIdLength).first() }
         .filter { it.type == Segment.Spec.value }
         .map { DiscoverySelectors.selectClass(it.value) }
         .toList()
   }
}
