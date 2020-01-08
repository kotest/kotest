package io.kotest.runner.jvm

import io.github.classgraph.ClassGraph
import io.kotest.Project
import io.kotest.SpecClass
import io.kotest.core.spec.SpecConfiguration
import io.kotest.extensions.DiscoveryExtension
import org.slf4j.LoggerFactory
import java.net.URI
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate
import kotlin.reflect.KClass

/**
 * [DiscoveryRequest] describes how to discover test classes.
 *
 * @param uris a list of uris to act as a classpath roots to search
 * @param classNames if specified then these classnames will be used instead of searching
 * @param packages if specified then only classes in the given packages will be considered
 * @param classNameFilters list of class name filters
 */
data class DiscoveryRequest(
   val uris: List<URI> = emptyList(),
   val classNames: List<String> = emptyList(),
   val packages: List<String> = emptyList(),
   val classNameFilters: List<Predicate<String>> = emptyList(),
   val packageFilters: List<Predicate<String>> = emptyList()
)

/**
 * Contains [SpecClass] classes discovered as part of a discovery request scan.
 */
data class DiscoveryResult(
   val classes: List<KClass<out SpecClass>>,
   val specs: List<KClass<out SpecConfiguration>>
)

/**
 * Scans for tests as specified by a [DiscoveryRequest].
 * [DiscoveryExtension] `afterScan` functions are applied after the scan is complete to
 * optionally filter the returned classes.
 */
object TestDiscovery {

   private val logger = LoggerFactory.getLogger(this.javaClass)
   private val requests = ConcurrentHashMap<DiscoveryRequest, DiscoveryResult>()

   fun discover(request: DiscoveryRequest): DiscoveryResult = requests.getOrPut(request) {

      val fromClassNames = loadClasses(request.classNames)
      logger.trace("Loaded ${fromClassNames.size} classes from classnames...")

      val fromClassPaths = if (request.uris.isEmpty() && request.classNames.isNotEmpty())
         emptyList()
      else
         scanUris(request.uris)
      logger.trace("Scan discovered ${fromClassPaths.size} classes in the classpaths...")

      val fromPackages = if (request.packages.isEmpty()) emptyList() else scanPackages(request.packages)
      logger.trace("Scan discovered ${fromClassPaths.size} classes by package...")

      val filtered = (fromClassNames + fromClassPaths + fromPackages)
         .asSequence()
         .filter { klass -> request.classNameFilters.isEmpty() || request.classNameFilters.all { it.test(klass.java.canonicalName) } }
         .filter { klass -> request.packageFilters.isEmpty() || request.packageFilters.all { it.test(klass.java.`package`.name) } }
         .filter { klass -> request.packages.isEmpty() || request.packages.any { klass.java.canonicalName.startsWith("$it.") } }
         .filter { SpecConfiguration::class.java.isAssignableFrom(it.java) }
         // must filter out abstract classes to avoid the spec parent classes themselves
         .filter { !it.isAbstract }
         // keep only class instances and not objects
         .filter { it.objectInstance == null }
         .toList()

      logger.trace("After filters there are ${filtered.size} spec classes")

      val afterExtensions = Project.discoveryExtensions()
         .fold(filtered) { cl, ext -> ext.afterScan(cl) }
         .sortedBy { it.simpleName }
      logger.trace("After discovery extensions there are ${filtered.size} spec classes")

      DiscoveryResult(emptyList(), afterExtensions)
   }

   /**
    * Returns a list of [SpecClass] classes detected using classgraph for the given packages
    */
   private fun scanPackages(packages: List<String>): List<KClass<out SpecConfiguration>> {

      val scanResult = ClassGraph()
         .enableClassInfo()
         .enableExternalClasses()
         .blacklistPackages("java.*", "javax.*", "sun.*", "com.sun.*", "kotlin.*")
         .whitelistPackages(*packages.toTypedArray())
         .scan()

      return scanResult
         .getClassesImplementing(SpecConfiguration::class.java.canonicalName)
         .map { Class.forName(it.name).kotlin }
         .filterIsInstance<KClass<out SpecConfiguration>>()
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
         .getClassesImplementing(SpecConfiguration::class.java.canonicalName)
         .map { Class.forName(it.name).kotlin }
         .filterIsInstance<KClass<out SpecConfiguration>>()
   }

   /**
    * Returns a list of [SpecConfiguration] KClasse created from the input list of fully qualified class names.
    */
   private fun loadClasses(classes: List<String>): List<KClass<out SpecConfiguration>> =
      classes.map { Class.forName(it).kotlin }
         .filterIsInstance<KClass<out SpecConfiguration>>()
}
