package io.kotest.runner.junit5

import io.kotest.core.engine.DiscoveryFilter
import io.kotest.core.engine.DiscoveryRequest
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.discovery.ClassNameFilter
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.ClasspathRootSelector
import org.junit.platform.engine.discovery.DirectorySelector
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.discovery.NestedMethodSelector
import org.junit.platform.engine.discovery.PackageNameFilter
import org.junit.platform.engine.discovery.PackageSelector
import org.junit.platform.engine.discovery.UriSelector
import org.junit.platform.engine.discovery.UniqueIdSelector

/**
 * Returns a Kotest [DiscoveryRequest] built from the selectors and filters present
 * in the JUnit [EngineDiscoveryRequest].
 *
 * Supported selectors are:
 *
 * - [ClassSelector] - used to specify a single class by fully qualified name
 * - [DirectorySelector] - classes are scanned in the given directory
 * - [UriSelector] - classes are scanned from the given uri
 * - [PackageSelector] - classes are scanned on the default classpath for the given package name
 *
 * Support filters are:
 *
 * - [ClassNameFilter] - filters out specs based on a classname
 * - [PackageNameFilter] - filters out specs based on package names
 *
 * Unsupported selectors are:
 *
 * - [MethodSelector] - not supported because kotest does not define tests as methods
 * - [NestedMethodSelector] - not supported becase kotest does not define tests as methods
 * - [UniqueIdSelector]
 * - [DirectorySelector]
 */
internal fun discoveryRequest(request: EngineDiscoveryRequest): DiscoveryRequest {

   // inside intellij when running a single test, we might be passed a class selector
   // and gradle will sometimes pass a class selector for each class it has detected
   val classnames = request.getSelectorsByType(ClassSelector::class.java).map { it.className }

   val packageFilters: List<DiscoveryFilter> = request.getSelectorsByType(PackageSelector::class.java).map {
      object : DiscoveryFilter {
         override fun test(className: String, packageName: String): Boolean = packageName == it.packageName
      }
   } + request.getFiltersByType(PackageNameFilter::class.java).map {
      object : DiscoveryFilter {
         override fun test(className: String, packageName: String): Boolean = it.toPredicate().test(packageName)
      }
   }

   val classnameFilters = request.getFiltersByType(ClassNameFilter::class.java).map {
      object : DiscoveryFilter {
         override fun test(className: String, packageName: String): Boolean = it.toPredicate().test(className)
      }
   }

   val filters = packageFilters + classnameFilters

   val uris = request.getSelectorsByType(ClasspathRootSelector::class.java).map { it.classpathRoot } +
      request.getSelectorsByType(DirectorySelector::class.java).map { it.path.toUri() } +
      request.getSelectorsByType(UriSelector::class.java).map { it.uri }

   val allowInternal = request.configurationParameters.get("allow_internal").isPresent
   return DiscoveryRequest(uris, classnames, filters, allowInternal)
}
