@file:Suppress("unused")

package io.kotest.framework.discovery

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.DiscoveryExtension
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.Spec
import io.kotest.mpp.log
import io.kotest.mpp.syspropOrEnv
import java.lang.management.ManagementFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Contains the results of a discovery request scan.
 *
 * @specs these are classes which extend one of the spec types
 * @scripts these are kotlin scripts which may or may not contain tests
 */
data class DiscoveryResult(
   val specs: List<KClass<out Spec>>,
   val scripts: List<KClass<*>>,
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
class Discovery(
   private val discoveryExtensions: List<DiscoveryExtension> = emptyList(),
   private val configuration: ProjectConfiguration,
) {

   private val requests = ConcurrentHashMap<DiscoveryRequest, DiscoveryResult>()

   // the results of a classpath scan, lazily executed and memoized.
   private val scanResult = lazy { classgraph().scan() }

   // filter functions
   //private val isScript: (KClass<*>) -> Boolean = { ScriptTemplateWithArgs::class.java.isAssignableFrom(it.java) }
   private val isSpecSubclassKt: (KClass<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it.java) }
   private val isSpecSubclass: (Class<*>) -> Boolean = { Spec::class.java.isAssignableFrom(it) }
   private val isAbstract: (KClass<*>) -> Boolean = { it.isAbstract }
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

//   /**
//    * Scans the classpaths for kotlin script files.
//    */
//   private fun discoverScripts(): List<KClass<out ScriptTemplateWithArgs>> {
//      log { "Discovery: Running script scan" }
//      return scanResult.value
//         .allClasses
//         .filter { it.extendsSuperclass(ScriptTemplateWithArgs::class.java.name) }
//         .map { it.load(false) }
//         .filter(isScript)
//         .filterIsInstance<KClass<out ScriptTemplateWithArgs>>()
//   }

   /**
    * Loads a class reference from a [ClassInfo].
    *
    * @param init false to avoid initializing the class
    */
   private fun ClassInfo.load(init: Boolean): KClass<out Any> =
      Class.forName(name, init, this::class.java.classLoader).kotlin

   private fun doDiscovery(request: DiscoveryRequest): Result<DiscoveryResult> = runCatching {

      val specClasses =
         if (request.onlySelectsSingleClasses()) loadSelectedSpecs(request) else fromClassPaths

      val filtered = specClasses
         .asSequence()
         .filter(selectorFn(request.selectors))
         .filter(filterFn(request.filters))
         // all classes must subclass one of the spec parents
         .filter(isSpecSubclassKt)
         // we don't want abstract classes
         .filterNot(isAbstract)
         .toList()

      log { "After filters there are ${filtered.size} spec classes" }

      log { "[Discovery] Further filtering classes via discovery extensions [$discoveryExtensions]" }

      val afterExtensions = discoveryExtensions
         .fold(filtered) { cl, ext -> ext.afterScan(cl) }
         .sortedBy { it.simpleName }

      log { "After discovery extensions there are ${afterExtensions.size} spec classes" }

//      val scriptsEnabled = System.getProperty(KotestEngineProperties.scriptsEnabled) == "true" ||
//         System.getenv(KotestEngineProperties.scriptsEnabled) == "true"

//      val scripts = when {
//         scriptsEnabled -> discoverScripts()
//         else -> emptyList()
//      }

      if (scanResult.isInitialized()) runCatching { scanResult.value.close() }

      log { "Discovery result [${afterExtensions.size} specs; scripts]" }
      DiscoveryResult(afterExtensions, emptyList(), null)
   }

   /**
    * Returns whether this is a request that selects single classes
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
      log { "Discovery: Loading specified classes..." }
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
      log { "Discovery: Loading of selected classes completed in ${duration}ms" }
      return loadedClasses
   }

   /**
    * Returns a list of [Spec] classes detected using classgraph in the list of
    * locations specified by the uris param.
    */
   private fun scanUris(): List<KClass<out Spec>> {

      log {
         val uptime = ManagementFactory.getRuntimeMXBean().uptime
         "Discovery: Starting test discovery scan... [uptime=$uptime]"
      }

      val result = scanResult.value
         .getSubclasses(Spec::class.java.name)
         .map { Class.forName(it.name).kotlin }
         .filterIsInstance<KClass<out Spec>>()

      log {
         val start = System.currentTimeMillis()
         val duration = System.currentTimeMillis() - start
         "Discovery: Test discovery completed in ${duration}ms"
      }

      return result
   }

   private fun classgraph(): ClassGraph {

      val cg = ClassGraph()
         .enableClassInfo()
         .enableExternalClasses()
         .ignoreClassVisibility()

      if (configuration.disableTestNestedJarScanning) {
         log { "Nested jar scanning is disabled" }
         cg.disableNestedJarScanning()
         cg.disableModuleScanning()
      }

      // do not change this to use reject as it will break clients using older versions of classgraph
      return cg.blacklistPackages(
         "java.*",
         "javax.*",
         "sun.*",
         "com.sun.*",
         "kotlin.*",
         "kotlinx.*",
         "androidx.*",
         "org.jetbrains.kotlin.*",
         "org.junit.*"
      ).apply {
         if (syspropOrEnv(KotestEngineProperties.disableJarDiscovery) == "true") {
            disableJarScanning()
         }
      }
   }
}
