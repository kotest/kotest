package io.kotest.runner.jvm

import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import io.kotest.core.*
import io.kotest.core.fp.getOrElse
import io.kotest.core.fp.orElse
import io.kotest.core.fp.some
import io.kotest.core.specs.Spec
import io.kotest.core.specs.SpecBuilder
import io.kotest.core.specs.SpecContainer
import io.kotest.extensions.DiscoveryExtension
import org.slf4j.LoggerFactory
import java.lang.reflect.Modifier
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
 * @param classNameFilters list of class name filters applied after scanning
 */
data class DiscoveryRequest(
   val uris: List<URI> = emptyList(),
   val classNames: List<String> = emptyList(),
   val packages: List<String> = emptyList(),
   val classNameFilters: List<Predicate<String>> = emptyList(),
   val packageFilters: List<Predicate<String>> = emptyList()
)

/**
 * Contains [SpecContainer] classes discovered as part of a discovery request scan.
 */
data class DiscoveryResult(val containers: List<SpecContainer>)

private fun SpecContainer.filterClassName(predicate: Predicate<String>) = when (this) {
   is SpecContainer.ValueSpec -> predicate.test(this.qualifiedName.orElse(this.simpleName).getOrElse("<unknown>"))
   is SpecContainer.ClassSpec -> predicate.test(this.kclass.java.canonicalName)
}

private fun SpecContainer.filterPackageName(predicate: Predicate<String>) = when (this) {
   is SpecContainer.ValueSpec -> predicate.test(this.packageName)
   is SpecContainer.ClassSpec -> predicate.test(this.kclass.java.`package`.name)
}

/**
 * Scans for tests as specified by a [DiscoveryRequest].
 * [DiscoveryExtension] `afterScan` functions are applied after the scan is complete to
 * optionally filter the returned [Spec]s.
 */
object TestDiscovery {

   private val logger = LoggerFactory.getLogger(this.javaClass)
   private val requests = ConcurrentHashMap<DiscoveryRequest, DiscoveryResult>()

   fun discover(request: DiscoveryRequest): DiscoveryResult = requests.getOrPut(request) {
      println("performing test discovery $request")
      val specs = when {
         // if we have class names in the request then only those classes directly are considered
         request.classNames.isNotEmpty() -> loadByClassName(request.classNames).apply {
            println("Loaded $size classes from classnames...")
         }
         // otherwise we scan the classpath and then filter down
         else -> {
            val result = scan()
            val specs = result.findClassSpecs() + result.findValueSpecs()
            println("Scan discovered ${specs.size} specs in the classpaths before filtering...")
            specs
         }
      }

      val filtered = specs
         .filter { container -> request.classNameFilters.all { container.filterClassName(it) } }
         .filter { container -> request.packageFilters.all { container.filterPackageName(it) } }
      println("After filters there are ${filtered.size} specs")

      val afterExtensions = Project.discoveryExtensions()
         .fold(filtered) { cl, ext -> ext.afterScan(cl) }
         .sortedBy { it.name.value }
      logger.info("After discovery extensions there are ${afterExtensions.size} spec classes")

      DiscoveryResult(afterExtensions)
   }

   private fun scan() = ClassGraph()
      .enableClassInfo()
      .enableExternalClasses()
      .ignoreClassVisibility()
      .blacklistPackages("java.*", "javax.*", "sun.*", "com.sun.*", "kotlin.*", "io.kotest.*")
      .scan()

   /**
    * Returns the top level public value [Spec]s.
    */
   private fun ScanResult.findValueSpecs(): List<SpecContainer> {
      return allClasses
         // top level vals get compiled into a class with the Kt extension,
         // so strip the others out for speed of searching
         .filter { it.name.endsWith("Kt") }
         .flatMap { info ->
            try {
               Class.forName(info.name)
                  .declaredFields
                  // we only want public vals, so private can be used to hide them from discovery if required
                  .filter { Modifier.isPublic(it.modifiers) }
                  /// top level vals are statics inside the class
                  .filter { Modifier.isStatic(it.modifiers) }
                  .filter { it.type.canonicalName == Spec::class.java.canonicalName }
                  .map { field ->
                     SpecContainer.ValueSpec(
                        lazy { field.get(null) as Spec },
                        info.packageName,
                        info.name.some(),
                        info.simpleName.some(),
                        info.resource.path
                     )
                  }
            } catch (e: Exception) {
               emptyList<SpecContainer>()
            }
         }
   }

   /**
    * Returns public [SpecBuilder] classes as instances of [SpecContainer].
    */
   @Suppress("UNCHECKED_CAST")
   private fun ScanResult.findClassSpecs(): List<SpecContainer> {
      return allClasses
         .filter { info -> info.superclasses.map { it.name }.contains(SpecBuilder::class.java.canonicalName) }
         .map { Class.forName(it.name).kotlin }
         // must filter out abstract classes to avoid the spec parent classes themselves
         .filter { it.java.isPublic() && it.java.isConcrete() }
         // all specs must have SpecBuilder as a parent superclass
         .filter { it.java.isSubclassOf(SpecBuilder::class.java) }
         // keep only class instances and not objects
         .filter { it.objectInstance == null }
         .filterIsInstance<KClass<out SpecBuilder>>()
         .map { SpecContainer.ClassSpec(it) }
   }

   /**
    * Returns a list of [SpecBuilder] classes from the given list of class names.
    * The input must be a list of fully qualified classnames.
    */
   private fun loadByClassName(classes: List<String>): List<SpecContainer> =
      classes.map { Class.forName(it).kotlin }
         // must filter out abstract classes to avoid the spec parent classes themselves
         .filter { it.java.isPublic() && it.java.isConcrete() }
         // only keep classes that subclass the right class
         .filter { it.java.isSubclassOf(SpecBuilder::class.java) }
         .filterIsInstance<KClass<out SpecBuilder>>()
         .map { SpecContainer.ClassSpec(it) }
}
