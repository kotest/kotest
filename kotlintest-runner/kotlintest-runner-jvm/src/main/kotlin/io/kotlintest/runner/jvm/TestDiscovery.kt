package io.kotlintest.runner.jvm

import io.github.classgraph.ClassGraph
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.extensions.DiscoveryExtension
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * [DiscoveryRequest] describes how to discover test classes.
 *
 * @param uris a list of uris to act as a classpath roots to search
 * @param classNames if specified then these classes will be used instead of searching
 * @param classNameFilters list of class name filters
 */
data class DiscoveryRequest(val uris: List<URI>, val classNames: List<String>, val classNameFilters: List<((String) -> Boolean)>)

data class DiscoveryResult(val classes: List<KClass<out Spec>>)

/**
 * Scans for tests as specified by a [DiscoveryRequest].
 * [DiscoveryExtension] `afterScan` functions are applied after the scan is complete to
 * optionally filter the returned classes.
 */
object TestDiscovery {

  private val logger = LoggerFactory.getLogger(this.javaClass)
  private val requests = ConcurrentHashMap<DiscoveryRequest, DiscoveryResult>()

  // returns all the locatable specs for the given uris
  private fun scan(uris: List<URI>): List<KClass<out Spec>> {
    val scanResult = ClassGraph()
        .verbose()                   // Log to stderr
        .enableClassInfo()
        .blacklistPackages("java.*", "javax.*", "sun.*", "com.sun.*", "kotlin.*")
        .whitelistPaths(* uris.map { it.path }.toTypedArray())
        .scan()
    return scanResult.getClassesImplementing(Spec::class.java.canonicalName)
        .map { Class.forName(it.name).kotlin as KClass<out Spec> }
  }

  private fun loadClasses(classes: List<String>): List<KClass<out Spec>> =
      classes.map { Class.forName(it).kotlin }
          .filterIsInstance<KClass<out Spec>>()

  private fun scan(request: DiscoveryRequest): List<KClass<out Spec>> {

    val classes = when {
      request.classNames.isNotEmpty() -> loadClasses(request.classNames).apply {
        logger.debug("Loaded ${this.size} classes from classnames...")
      }
      else -> scan(request.uris).apply {
        logger.debug("Scan discovered ${this.size} classes...")
      }
    }

    val specs = classes
        .filter { Spec::class.java.isAssignableFrom(it.java) }
        // must filter out abstract classes to avoid the spec parent classes themselves
        .filter { !it.isAbstract }
        // keep only class instances
        .filter { it.objectInstance == null }

    logger.debug("...which has filtered to ${specs.size} non abstract classes")

    val extensions = Project.discoveryExtensions()
    val filtered = extensions
        .fold(specs) { cl, ext -> ext.afterScan(cl) }
        .sortedBy { it.simpleName }

    logger.debug("${filtered.size} classes after applying discovery extensions")
    return filtered
  }

  fun discover(request: DiscoveryRequest): DiscoveryResult =
      requests.getOrPut(request) {
        val classes = scan(request)
        DiscoveryResult(classes)
      }
}