package io.kotest.engine.config

import io.kotest.core.extensions.Extension
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.core.annotation.AutoScan
import io.kotest.mpp.instantiateOrObject

/**
 *
 * Applies listeners, filters and extensions detected during scanning, that are annotated
 * with the [AutoScan] annotation.
 *
 * Note: This function will have no effect on non-JVM targets.
 */
internal actual fun applyConfigFromAutoScan(configuration: MutableConfiguration) {

   // this property is used to disable class path scanning for configurations
   if (System.getProperty(KotestEngineProperties.disableAutoScanClassPathScanning) == "true")
      return

   val cg = classgraph()
   val scan = cg.scan()
   scan.getClassesWithAnnotation(AutoScan::class.java.name)
      .map { Class.forName(it.name) }
      .mapNotNull { instantiateOrObject(it).getOrNull() }
      .filterIsInstance<Extension>().forEach {
         configuration.registry().add(it)
      }
   scan.close()
}
