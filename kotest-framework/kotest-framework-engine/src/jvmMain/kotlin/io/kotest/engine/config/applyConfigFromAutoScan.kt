package io.kotest.engine.config

import io.kotest.core.config.Configuration
import io.kotest.core.extensions.Extension
import io.kotest.core.filter.Filter
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.spec.AutoScan
import io.kotest.mpp.instantiateOrObject

/**
 *
 * Applies listeners, filters and extensions detected during scanning, that are annotated
 * with the [AutoScan] annotation.
 *
 * Note: This function will have no effect on non-JVM targets.
 */
actual fun applyConfigFromAutoScan(configuration: Configuration) {

   // this property is used to disable class path scanning for configurations
   if (System.getProperty(KotestEngineProperties.disableAutoScanClassPathScanning) == "true")
      return

   val autoscanned = classgraph().scan()
      .getClassesWithAnnotation(AutoScan::class.java.name)
      .map { Class.forName(it.name) }
      .mapNotNull { instantiateOrObject(it).getOrNull() }

   val filters = autoscanned.filterIsInstance<Filter>()
   val extensions = autoscanned.filterIsInstance<Extension>()

   configuration.registerFilters(filters)
   configuration.registerExtensions(extensions)
}
