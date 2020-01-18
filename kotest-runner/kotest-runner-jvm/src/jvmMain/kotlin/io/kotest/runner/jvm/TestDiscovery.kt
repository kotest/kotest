package io.kotest.runner.jvm

import io.github.classgraph.ClassGraph
import io.kotest.core.config.Project
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.extensions.DiscoveryExtension
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

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
   private val isClass: (KClass<*>) -> Boolean = { it.objectInstance == null }

   // we filter generally scanned classes for public only, but classes lookup by name can be anything
   private val isInternal: (KClass<*>) -> Boolean = { it.visibility == KVisibility.INTERNAL }
   private val isPublic: (KClass<*>) -> Boolean = { Modifier.isPublic(it.java.modifiers) }
   private val isPrivate: (KClass<*>) -> Boolean = { Modifier.isPrivate(it.java.modifiers) }

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
         .filter(isClass)
         .toList()

      logger.trace("After filters there are ${filtered.size} spec classes")

      val afterExtensions = Project.discoveryExtensions()
         .fold(filtered) { cl, ext -> ext.afterScan(cl) }
         .sortedBy { it.simpleName }
      logger.trace("After discovery extensions there are ${filtered.size} spec classes")

      logger.debug("Discovery is returning ${afterExtensions.size} specs")
      DiscoveryResult(afterExtensions)
   }

   /**
    * Returns a list of [SpecConfiguration] classes detected using classgraph in the list of
    * locations specified by the uris param.
    */
   @UseExperimental(ExperimentalTime::class)
   private fun scanUris(uris: List<URI>): List<KClass<out SpecConfiguration>> {
      logger.debug("Starting test discovery scan...")
      val (scanResult, time) = measureTimedValue {
         ClassGraph()
            .enableClassInfo()
            .enableExternalClasses()
            .blacklistPackages("java.*", "javax.*", "sun.*", "com.sun.*", "kotlin.*")
            .scan()
      }
      logger.debug("Test discovery competed in $time")

      return scanResult
         .getSubclasses(SpecConfiguration::class.java.name)
         .map { Class.forName(it.name).kotlin }
         .filter(isPublic)
         .filterNot(isInternal)
         .filterNot(isPrivate)
         .filterIsInstance<KClass<out SpecConfiguration>>()
   }

   /**
    * Returns a list of [SpecConfiguration] classes created from the input list of fully qualified class names.
    */
   private fun loadClasses(classes: List<String>): List<KClass<out SpecConfiguration>> =
      classes.map { Class.forName(it).kotlin }
         .filterIsInstance<KClass<out SpecConfiguration>>()
}
