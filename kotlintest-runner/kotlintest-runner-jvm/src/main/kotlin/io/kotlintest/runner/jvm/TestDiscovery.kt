package io.kotlintest.runner.jvm

import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.extensions.DiscoveryExtension
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.FilterBuilder
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
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

  init {
    ReflectionsHelper.registerUrlTypes()
  }

  private fun reflections(uris: List<URI>): Reflections {

    val classOnly = { name: String? -> name?.endsWith(".class") ?: false }
    val excludeJDKPackages = FilterBuilder.parsePackages("-java, -javax, -sun, -com.sun")

    val executor = Executors.newSingleThreadExecutor()
    val classes = Reflections(ConfigurationBuilder()
        .addUrls(uris.map { it.toURL() })
        .setExpandSuperTypes(true)
        .setExecutorService(executor)
        .filterInputsBy(excludeJDKPackages.add(classOnly))
        .setScanners(SubTypesScanner()))
    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.HOURS)
    return classes
  }

  // returns all the locatable specs for the given uris
  private fun scan(uris: List<URI>): List<KClass<out Spec>> =
      reflections(uris)
          .getSubTypesOf(Spec::class.java)
          .map(Class<out Spec>::kotlin)

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
        .fold(specs, { cl, ext -> ext.afterScan(cl) })
        .sortedBy { it.simpleName }

    logger.debug("${filtered.size} classes after applying discovery extensions")
    return filtered
  }

  fun discover(request: DiscoveryRequest): DiscoveryResult =
      requests.getOrPut(request, {
        val classes = scan(request)
        DiscoveryResult(classes)
      })
}