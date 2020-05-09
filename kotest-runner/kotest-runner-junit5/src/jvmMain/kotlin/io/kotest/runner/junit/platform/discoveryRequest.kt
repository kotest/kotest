package io.kotest.runner.junit.platform

import io.kotest.core.engine.discovery.DiscoveryFilter
import io.kotest.core.engine.discovery.DiscoveryRequest
import io.kotest.core.engine.discovery.DiscoverySelector
import io.kotest.core.engine.discovery.Modifier
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.discovery.ClassNameFilter
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.DirectorySelector
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.discovery.NestedMethodSelector
import org.junit.platform.engine.discovery.PackageNameFilter
import org.junit.platform.engine.discovery.PackageSelector
import org.junit.platform.engine.discovery.UniqueIdSelector
import org.junit.platform.engine.discovery.UriSelector

/**
 * Returns a Kotest [DiscoveryRequest] built from the selectors and filters present
 * in the JUnit Platform [EngineDiscoveryRequest].
 *
 * Supported selectors are:
 *
 * - [ClassSelector] - used to specify a single class by fully qualified name
 * - [DirectorySelector] - classes are scanned in the given directory
 * - [UriSelector] - classes are scanned from the given uri
 * - [PackageSelector] - classes are scanned on the default classpath for the given package name
 *
 * Supported filters are:
 *
 * - [ClassNameFilter] - filters out specs based on a classname
 * - [PackageNameFilter] - filters out specs based on package names
 *
 * Unsupported selectors are:
 *
 * - [MethodSelector] - not supported because kotest does not define tests as methods
 * - [NestedMethodSelector] - not supported becase kotest does not define tests as methods
 * - [UniqueIdSelector] - not supported becase kotest does not assign ids to tests
 * - [DirectorySelector] - not supported becase kotest is not directory based
 */
internal fun createDiscoveryRequest(request: EngineDiscoveryRequest): DiscoveryRequest {

   val packageSelectors = request.getSelectorsByType(PackageSelector::class.java).map {
      DiscoverySelector.PackageDiscoverySelector(it.packageName)
   }

   val classSelectors = request.getSelectorsByType(ClassSelector::class.java).map {
      DiscoverySelector.ClassDiscoverySelector(it.className)
   }

   val classFilters = request.getFiltersByType(ClassNameFilter::class.java).map { filter ->
      DiscoveryFilter.ClassNameDiscoveryFilter { filter.toPredicate().test(it.value) }
   }

   val packageFilters = request.getFiltersByType(PackageNameFilter::class.java).map { filter ->
      DiscoveryFilter.PackageNameDiscoveryFilter { filter.toPredicate().test(it.value) }
   }

   val private = if (request.configurationParameters.get("allow_private").isPresent) Modifier.Private else null
   val modifiers = listOfNotNull(Modifier.Public, Modifier.Internal, private)
   val modifiersFilter = DiscoveryFilter.ClassModifierDiscoveryFilter(modifiers.toSet())

   val filters = packageFilters + classFilters + modifiersFilter
   val selectors = packageSelectors + classSelectors

   return DiscoveryRequest(selectors, filters)
}
