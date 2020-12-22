@file:Suppress("unused")

package io.kotest.framework.discovery

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ScanResult
import io.kotest.core.extensions.DiscoveryExtension
import io.kotest.core.internal.KotestEngineSystemProperties
import io.kotest.core.spec.Spec
import io.kotest.fp.Try
import io.kotest.fp.getOrElse
import java.lang.management.ManagementFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.script.templates.standard.ScriptTemplateWithArgs

/**
 * Contains the results of a discovery request scan.
 *
 * @specs these are classes which extend one of the spec types
 * @scripts these are kotlin scripts which may or may not contain tests
 */
data class DiscoveryResult(
   val specs: List<KClass<out Spec>>,
   val scripts: List<KClass<out ScriptTemplateWithArgs>>,
   val error: Throwable?, // this error is set if there was an exception during discovery
) {
   companion object {
      fun error(t: Throwable): DiscoveryResult = DiscoveryResult(emptyList(), emptyList(), t)
   }
}

/**
 * Scans for tests as specified by a [DiscoveryRequest].
 *
 * [DiscoveryExtension] `afterScan` functions are applied after the scan is complete to
 * optionally filter the returned classes.
 */
class Discovery(private val discoveryExtensions: List<DiscoveryExtension> = emptyList()) {

   private val requests = ConcurrentHashMap<DiscoveryRequest, DiscoveryResult>()

   // the results of a classpath scan, lazily executed and memoized.
   private val scanResult by lazy { scan() }

   // filter functions
   private val isScript: (KClass<*>) -> Boolean = { ScriptTemplateWithArgs::class.java.isAssignableFrom(it.java) }
   private val isSpecSubclassKt: (KClass<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it.java) }
   private val isSpecSubclass: (Class<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it) }
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

   fun discover(request: DiscoveryRequest): DiscoveryResult =
      requests.getOrPut(request) { doDiscovery(request).getOrElse { DiscoveryResult.error(it) } }

   /**
    * Scans the classpaths for kotlin script files.
    */
   private fun discoverScripts(): List<KClass<out ScriptTemplateWithArgs>> {
      log("Discovery: Running script scan")
      return scanResult
         .allClasses
         .filter { it.extendsSuperclass(ScriptTemplateWithArgs::class.java.name) }
         .map { it.load(false) }
         .filter(isScript)
         .filterIsInstance<KClass<out ScriptTemplateWithArgs>>()
   }

   /**
    * Loads a class reference from a [ClassInfo].
    * @param init set to false to avoid initializing the class
    */
   private fun ClassInfo.load(init: Boolean): KClass<out Any> =
      Class.forName(name, init, this::class.java.classLoader).kotlin

   private fun doDiscovery(request: DiscoveryRequest): Try<DiscoveryResult> = Try {

      val specClasses =
         if (request.onlySelectsSingleClasses()) loadSelectedSpecs(request) else fromClassPaths

      val filtered = specClasses
         .asSequence()
         .filter(selectorFn(request.selectors))
         .filter(filterFn(request.filters))
         .filter(isSpecSubclassKt)
         .filter(isClass)
         .filterNot(isAbstract)
         .toList()

      log("After filters there are ${filtered.size} spec classes")

      val afterExtensions = discoveryExtensions
         .fold(filtered) { cl, ext -> ext.afterScan(cl) }
         .sortedBy { it.simpleName }

      log("After discovery extensions there are ${filtered.size} spec classes")

      val scripts = when {
         System.getProperty(KotestEngineSystemProperties.scriptsEnabled) == "true" -> discoverScripts()
         System.getenv(KotestEngineSystemProperties.scriptsEnabled) == "true" -> discoverScripts()
         else -> emptyList()
      }

      log("Discovery result [${afterExtensions.size} specs; ${scripts.size} scripts]")
      DiscoveryResult(afterExtensions, scripts, null)
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
      log("Discovery: Loading specified classes...")
      val start = System.currentTimeMillis()

      // first filter down to spec instances only, then load the full class
      val loadedClasses = request
         .selectors
         .asSequence()
         .filterIsInstance<DiscoverySelector.ClassDiscoverySelector>()
         .map { Class.forName(it.className, false, this::class.java.classLoader) }
         .filter(isSpecSubclass)
         .map { Class.forName(it.name).kotlin }
         .filterIsInstance<KClass<out Spec>>()
         .toList()

      val duration = System.currentTimeMillis() - start
      log("Discovery: Loading of selected classes completed in ${duration}ms")
      return loadedClasses
   }

   /**
    * Returns a list of [Spec] classes detected using classgraph in the list of
    * locations specified by the uris param.
    */
   private fun scanUris(): List<KClass<out Spec>> {
      val uptime = ManagementFactory.getRuntimeMXBean().uptime
      log("Discovery: Starting test discovery scan... [uptime=$uptime]")
      val start = System.currentTimeMillis()
      val duration = System.currentTimeMillis() - start
      log("Discovery: Test discovery completed in ${duration}ms")

      return scanResult
         .getSubclasses(Spec::class.java.name)
         .map { Class.forName(it.name).kotlin }
         .filterIsInstance<KClass<out Spec>>()
   }

   private fun scan(): ScanResult {
      return ClassGraph()
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
         ).scan()
      val duration = System.currentTimeMillis() - start
      log("Discovery: Test discovery completed in ${duration}ms")

      return scanResult.use { result ->
         result.getSubclasses(Spec::class.java.name)
            .map { Class.forName(it.name).kotlin }
            .filterIsInstance<KClass<out Spec>>()
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
