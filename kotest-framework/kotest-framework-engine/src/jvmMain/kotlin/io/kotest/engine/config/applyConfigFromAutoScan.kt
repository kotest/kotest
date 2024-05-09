package io.kotest.engine.config

import io.kotest.core.annotation.AutoScan
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.Extension
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.mpp.instantiateOrObject
import io.kotest.mpp.log

/**
 *
 * Applies listeners, filters and extensions detected during scanning, that are annotated
 * with the [AutoScan] annotation.
 *
 * Note: This function will have no effect on non-JVM targets.
 */
internal actual fun applyConfigFromAutoScan(configuration: ProjectConfiguration) {

   // this property is used to disable class path scanning for configurations
   if (System.getProperty(KotestEngineProperties.disableAutoScanClassPathScanning) == "true") {
      log { "Kotest auto classpath scanning is disabled" }
      return
   }

   println("Warning: Kotest autoscan is enabled. This means Kotest will scan the classpath for extensions that are "
      + "annotated with @AutoScan. To avoid this startup cost, set autoscan to false by setting the system property "
      + "'${KotestEngineProperties.disableAutoScanClassPathScanning}=true'. "
      + "In 6.0 this value will default to true. For further details see "
      + "https://kotest.io/docs/next/framework/project-config.html#runtime-detection"
   )

   classgraph().scan().use { result ->
      result.getClassesWithAnnotation(AutoScan::class.java.name)
         .map { Class.forName(it.name) }
         .mapNotNull { instantiateOrObject(it).getOrNull() }
         .filterIsInstance<Extension>().forEach {
            configuration.registry.add(it)
         }
   }
}
