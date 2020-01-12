package io.kotest.runner.jvm

import io.github.classgraph.ClassGraph
import io.kotest.Project
import io.kotest.SpecClass
import io.kotest.core.spec.SpecConfiguration
import io.kotest.extensions.DiscoveryExtension
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

interface DiscoveryFilter {
   fun test(className: String, packageName: String): Boolean
}

/**
 * [DiscoveryRequest] describes how to discover test classes.
 *
 * @param uris a list of uris to act as a classpath roots to search
 * @param classNames if specified then these classnames will be used instead of searching
 * @param filters list of predicates to filter the detected classes
 */
data class DiscoveryRequest(
   val uris: List<URI> = emptyList(),
   val classNames: List<String> = emptyList(),
   val filters: List<DiscoveryFilter> = emptyList()
)

/**
 * Contains [SpecConfiguration] classes discovered as part of a discovery request scan.
 */
data class DiscoveryResult(val specs: List<KClass<out SpecConfiguration>>)

/**
 * Scans for tests as specified by a [DiscoveryRequest].
 * [DiscoveryExtension] `afterScan` functions are applied after the scan is complete to
 * optionally filter the returned classes.
 */
object TestDiscovery {

   private val logger = LoggerFactory.getLogger(this.javaClass)
   private val requests = ConcurrentHashMap<DiscoveryRequest, DiscoveryResult>()

   // filter functions
   private val specOnly: (KClass<*>) -> Boolean = { SpecConfiguration::class.java.isAssignableFrom(it.java) }
   private val isAbstract: (KClass<*>) -> Boolean = { it.isAbstract }
   private val isPublic: (KClass<*>) -> Boolean = { Modifier.isPublic(it.java.modifiers) }
   private val isClass: (KClass<*>) -> Boolean = { it.objectInstance == null }

   fun discover(request: DiscoveryRequest): DiscoveryResult = requests.getOrPut(request) {

      val fromClassNames = loadClasses(request.classNames)
      logger.trace("Loaded ${fromClassNames.size} classes from classnames...")

      val fromClassPaths = if (request.uris.isEmpty() && request.classNames.isNotEmpty())
         emptyList()
      else
         scanUris(request.uris)
      logger.trace("Scan discovered ${fromClassPaths.size} classes in the classpaths...")

      val requestFilters: (KClass<*>) -> Boolean = { klass ->
         request.filters.isEmpty() || request.filters.all {
            it.test(klass.java.canonicalName, klass.java.`package`.name)
         }
      }

      val filtered = (fromClassNames + fromClassPaths)
         .asSequence()
         .filter(requestFilters)
         .filter(specOnly)
         .filterNot(isAbstract)
         .filter(isPublic)
         .filter(isClass)
         .toList()

      logger.trace("After filters there are ${filtered.size} spec classes")

      val afterExtensions = Project.discoveryExtensions()
         .fold(filtered) { cl, ext -> ext.afterScan(cl) }
         .sortedBy { it.simpleName }
      logger.trace("After discovery extensions there are ${filtered.size} spec classes")

      DiscoveryResult(afterExtensions)
   }

   /**
    * Returns a list of [SpecConfiguration] classes detected using classgraph in the list of
    * locations specified by the uris param.
    */
   private fun scanUris(uris: List<URI>): List<KClass<out SpecConfiguration>> {

      val scanResult = ClassGraph()
         .enableClassInfo()
         .enableExternalClasses()
         .blacklistPackages("java.*", "javax.*", "sun.*", "com.sun.*", "kotlin.*")
         .scan()

      return scanResult
         .getSubclasses(SpecConfiguration::class.java.name)
         .map { Class.forName(it.name).kotlin }
         .filterIsInstance<KClass<out SpecConfiguration>>()
   }

   /**
    * Returns a list of [SpecConfiguration] classes created from the input list of fully qualified class names.
    */
   private fun loadClasses(classes: List<String>): List<KClass<out SpecConfiguration>> =
      classes.map { Class.forName(it).kotlin }
         .filterIsInstance<KClass<out SpecConfiguration>>()
}
