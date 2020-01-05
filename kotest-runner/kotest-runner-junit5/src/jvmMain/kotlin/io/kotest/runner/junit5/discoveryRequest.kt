package io.kotest.runner.junit5

import io.kotest.runner.jvm.DiscoveryRequest
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.discovery.ClassNameFilter
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.ClasspathRootSelector
import org.junit.platform.engine.discovery.DirectorySelector
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.discovery.PackageNameFilter
import org.junit.platform.engine.discovery.PackageSelector
import org.junit.platform.engine.discovery.UriSelector

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
 * Supported filters are:
 *
 * - [ClassNameFilter] - filters out specs based on a classname
 * - [PackageNameFilter] - filters out specs based on package names
 *
 * Unsupported selectors are:
 *
 * - [MethodSelector] - not supported because kotest does not define tests as methods/functions
 */
internal fun EngineDiscoveryRequest.toDiscoveryRequest(): DiscoveryRequest {

  // inside intellij when running a single test, we might be passed a class selector
  // and gradle will sometimes pass a class selector for each class it has detected
  val classnames = getSelectorsByType(ClassSelector::class.java).map { it.className }

  val packages = getSelectorsByType(PackageSelector::class.java).map { it.packageName }

  val uris = getSelectorsByType(ClasspathRootSelector::class.java).map { it.classpathRoot } +
      getSelectorsByType(DirectorySelector::class.java).map { it.path.toUri() } +
      getSelectorsByType(UriSelector::class.java).map { it.uri }

  val classNameFilters = getFiltersByType(ClassNameFilter::class.java).map { it.toPredicate() }
  val packageFilters = getFiltersByType(PackageNameFilter::class.java).map { it.toPredicate() }

  return DiscoveryRequest(uris, classnames, packages, classNameFilters, packageFilters)
}
