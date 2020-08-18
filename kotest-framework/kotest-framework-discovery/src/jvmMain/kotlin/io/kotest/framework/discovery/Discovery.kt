@file:Suppress("unused")

package io.kotest.framework.discovery

import io.github.classgraph.ClassGraph
import io.kotest.core.extensions.DiscoveryExtension
import io.kotest.core.spec.Spec
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Contains [Spec] classes discovered as part of a discovery request scan.
 */
data class DiscoveryResult(val specs: List<KClass<out Spec>>)

/**
 * Scans for tests as specified by a [DiscoveryRequest].
 *
 * [DiscoveryExtension] `afterScan` functions are applied after the scan is complete to
 * optionally filter the returned classes.
 */
class Discovery(private val discoveryExtensions: List<DiscoveryExtension> = emptyList()) {

   private val requests = ConcurrentHashMap<DiscoveryRequest, DiscoveryResult>()

   // filter functions
   private val isSpecSubclass: (KClass<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it.java) }
   private val isAbstract: (KClass<*>) -> Boolean = { it.isAbstract }
   private val isClass: (KClass<*>) -> Boolean = { it.objectInstance == null }
   private val fromClassPaths: List<KClass<out Spec>> by lazy { scanUris() }

   /**
    * Returns a function that applies all the [DiscoveryFilter]s to a given class.
    * The class must pass all the filters to be included.
    */
   private fun filterFn(filters: List<DiscoveryFilter>): (KClass<out Spec>) -> Boolean = { kclass ->
      filters.isEmpty() || filters.all { it.test(kclass) }
   }

   /**
    * Returns a function that applies all the [DiscoverySelector]s to a given class.
    * The class must pass any one selector to be included.
    */
   private fun selectorFn(selectors: List<DiscoverySelector>): (KClass<out Spec>) -> Boolean = { kclass ->
      selectors.isEmpty() || selectors.any { it.test(kclass) }
   }

   fun discover(request: DiscoveryRequest): DiscoveryResult = requests.getOrPut(request) { doDiscovery(request) }

   private fun doDiscovery(request: DiscoveryRequest): DiscoveryResult {

      val specClasses =
         if (request.onlySelectsSingleClasses()) loadSelectedSpecs(request) else fromClassPaths

      val filtered = specClasses
         .asSequence()
         .filter(selectorFn(request.selectors))
         .filter(filterFn(request.filters))
         .filter(isSpecSubclass)
         .filter(isClass)
         .filterNot(isAbstract)
         .toList()

      log("After filters there are ${filtered.size} spec classes")

      val afterExtensions = discoveryExtensions
         .fold(filtered) { cl, ext -> ext.afterScan(cl) }
         .sortedBy { it.simpleName }

      log("After discovery extensions there are ${filtered.size} spec classes")

      log("Discovery is returning ${afterExtensions.size} specs")
      return DiscoveryResult(afterExtensions)
   }

   /**
    * Returns whether or not this is a requests that selects single classes
    * only. Used to avoid full classpath scans when not necessary.
    */
   private fun DiscoveryRequest.onlySelectsSingleClasses(): Boolean =
      selectors.isNotEmpty() &&
         selectors.all { it is DiscoverySelector.ClassDiscoverySelector }

   /**
    * Returns a list of [Spec] classes from discovery requests that only have
    * selectors of type [DiscoverySelector.ClassDiscoverySelector].
    */
   private fun loadSelectedSpecs(request: DiscoveryRequest): List<KClass<out Spec>> {
      log("Starting loading of selected tests...")
      val start = System.currentTimeMillis()
      val loadedClasses = request
         .selectors
         .map { Class.forName((it as DiscoverySelector.ClassDiscoverySelector).className).kotlin }
         .filter(isSpecSubclass)
         .filterIsInstance<KClass<out Spec>>()
      val duration = System.currentTimeMillis() - start
      log("Loading of selected tests completed in ${duration}ms")
      return loadedClasses
   }

   /**
    * Returns a list of [Spec] classes detected using classgraph in the list of
    * locations specified by the uris param.
    */
   private fun scanUris(): List<KClass<out Spec>> {
      log("Starting test discovery scan...")
      val start = System.currentTimeMillis()
      val scanResult = ClassGraph()
         .enableClassInfo()
         .enableExternalClasses()
         .ignoreClassVisibility()
         .rejectPackages(
            "java.*",
            "javax.*",
            "sun.*",
            "com.sun.*",
            "kotlin.*",
            "androidx.*",
            "org.jetbrains.kotlin.*",
            "org.junit.*"
         )
         .scan()
      val duration = System.currentTimeMillis() - start
      log("Test discovery completed in ${duration}ms")

      return scanResult.use { result ->
         result.getSubclasses(Spec::class.java.name)
            .map { Class.forName(it.name).kotlin }
            .filterIsInstance<KClass<out Spec>>()
      }
   }
}

private fun enabled() = System.getProperty("KOTEST_DEBUG") != null || System.getenv("KOTEST_DEBUG") != null

fun log(msg: String) = log(msg, null)

fun log(msg: String, t: Throwable?) {
   if (enabled()) {
      println(msg)
      if (t != null) println(t)
   }
}
