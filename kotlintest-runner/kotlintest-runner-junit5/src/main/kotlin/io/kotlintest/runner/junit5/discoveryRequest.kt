package io.kotlintest.runner.junit5

import io.kotlintest.runner.jvm.DiscoveryRequest
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
 * Returns a [DiscoveryRequest] built from all selectors present
 * in the engine request.
 *
 * Supported selectors are:
 *
 * - [ClassSelector] - used to specify a single class by fully qualified name
 * - [DirectorySelector] - classes are scanned in the given directory
 * - [UriSelector] - classes are scanned from the given uri
 * - [PackageSelector] - classes are limited to the given package name
 *
 * [MethodSelector] is not supported because kotlintest does not work
 * on the method level.
 */
internal fun discoveryRequest(request: EngineDiscoveryRequest): DiscoveryRequest {

  // inside intellij when running a single test, we might be passed a class selector
  // and gradle will sometimes pass a class selector for each class it has detected
  val classnames = request.getSelectorsByType(ClassSelector::class.java).map { it.className }

  val packages = request.getSelectorsByType(PackageSelector::class.java).map { it.packageName }

  val uris = request.getSelectorsByType(ClasspathRootSelector::class.java).map { it.classpathRoot } +
      request.getSelectorsByType(DirectorySelector::class.java).map { it.path.toUri() } +
      request.getSelectorsByType(UriSelector::class.java).map { it.uri }

  val classNameFilters = request.getFiltersByType(ClassNameFilter::class.java).map { it.toPredicate() }
  val packageFilters = request.getFiltersByType(PackageNameFilter::class.java).map { it.toPredicate() }

  return DiscoveryRequest(uris, classnames, packages, classNameFilters, packageFilters)
}