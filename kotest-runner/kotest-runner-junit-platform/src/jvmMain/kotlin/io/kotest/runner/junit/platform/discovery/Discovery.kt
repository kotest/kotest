package io.kotest.runner.junit.platform.discovery

import io.kotest.core.Logger
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.runner.junit.platform.Segment
import org.junit.platform.commons.support.ReflectionSupport
import org.junit.platform.engine.ConfigurationParameters
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.ClasspathRootSelector
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.engine.discovery.UniqueIdSelector
import kotlin.reflect.KClass

/**
 * Contains the results of a discovery scan.
 */
internal data class DiscoveryResult(
   val specs: List<SpecRef>,
)

/**
 * Converts JUnit selectors into Kotest specs.
 */
internal object Discovery {

   private val logger = Logger<Discovery>()

   // filter functions
   private val isSpecSubclass: (Class<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it) }
   private val isAbstract: (KClass<*>) -> Boolean = { it.isAbstract }

   fun discover(engineId: UniqueId, request: EngineDiscoveryRequest): DiscoveryResult {

      logger.log { "Starting spec discovery" }

      // kotest only supports classpath root, class and unique id selectors (which we convert to class selectors)
      val classpathRootSelectors = request.getSelectorsByType(ClasspathRootSelector::class.java)
      val classSelectors = request.getSelectorsByType(ClassSelector::class.java) +
         convertUniqueIdsToClassSelectors(engineId, request)

      val specsSelected = (specsFromClasspathRootSelectors(classpathRootSelectors) + specsFromClassSelectors(classSelectors))

      val specsAfterInitialFiltering = specsSelected.filter(
         filterFn(
            listOf(
               classVisibilityFilter(classSelectors, request.configurationParameters)
            )
         )
      )

      // Test retry tools (gradle test-retry, IntelliJ "Re-run Failed Tests") commonly send both
      // a ClassSelector for the spec AND a UniqueIdSelector for a test inside it. Without dedup
      // the spec would be selected twice — instantiated and executed twice — and JUnit Platform
      // would receive duplicate executionStarted events for the same UniqueId.
      val deduped = specsAfterInitialFiltering.distinct()

      logger.log { "${deduped.size} specs will be returned" }

      return DiscoveryResult(deduped.map { SpecRef.Reference(it, it.java.name) })
   }

   /**
    * Returns a [DiscoveryFilter] that filters specs based on their visibility.
    *
    * We normally filter out private classes, with two exceptions:
    * 1. If the configuration parameter "allow_private" is set to true, then private classes are also included.
    * 2. If there is only a single class, then we include it regardless of visibility, as this is most likely
    *    a test class that is being run directly from the IDE
    */
   private fun classVisibilityFilter(
      classSelectors: List<ClassSelector>,
      configurationParameters: ConfigurationParameters
   ): DiscoveryFilter {

      val allowPrivateConfigured = configurationParameters.get("allow_private")
         .map { it.toBoolean() }
         .orElse(false)
      val private = if (classSelectors.size == 1 || allowPrivateConfigured) Modifier.Private else null

      val modifiers = listOfNotNull(Modifier.Public, Modifier.Internal, private)
      return DiscoveryFilter.ClassModifierDiscoveryFilter(modifiers.toSet())
   }

   /**
    * Returns the request's [Spec]s if they are completely specified by classpath root selectors, null otherwise.
    */
   private fun specsFromClasspathRootSelectors(selectors: List<ClasspathRootSelector>): List<KClass<out Spec>> {
      val specs = selectors
         .flatMap { selector ->
            ReflectionSupport.findAllClassesInClasspathRoot(selector.classpathRoot, isSpecSubclass) { true }
         }
         .asSequence()
         .map(Class<*>::kotlin)
         .filterNot(isAbstract)
         .filterIsInstance<KClass<out Spec>>()
         .toList()

      logger.log { "Collected specs via ${selectors.size} classpath root discovery selectors: found ${specs.size} specs" }
      return specs
   }

   /**
    * Returns the request's [Spec]s if they are completely specified by class selectors, null otherwise.
    * JUnit provides a list of [ClassSelector]s, which we can use to discover specs.
    * We check that the classes are subclasses of [Spec] as the list may include JUnit classes or other
    * test framework classes.
    */
   private fun specsFromClassSelectors(selectors: List<ClassSelector>): List<KClass<out Spec>> {

      // first filter down to spec instances only, then load the full class
      val specs = selectors
         .asSequence()
         // must load the class without initializing it, as we just want to check if it's a subclass of Spec
         .mapNotNull { runCatching { Class.forName(it.className, false, this::class.java.classLoader) }.getOrNull() }
         .filter(isSpecSubclass)
         // now we can properly initialize it
         .map { Class.forName(it.name).kotlin }
         .filterNot(isAbstract)
         .filterIsInstance<KClass<out Spec>>()
         .toList()

      logger.log { "Collected specs via ${selectors.size} class selectors: found ${specs.size} specs" }
      return specs
   }

   /**
    * Returns a function that applies all the [DiscoveryFilter]s to a given class.
    * The class must pass all the filters to be included.
    */
   private fun filterFn(filters: List<DiscoveryFilter>): (KClass<out Spec>) -> Boolean = { kclass ->
      filters.isEmpty() || filters.all { it.test(kclass) }
   }

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
