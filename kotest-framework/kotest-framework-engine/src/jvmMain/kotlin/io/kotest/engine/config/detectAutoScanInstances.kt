package io.kotest.engine.config

import io.github.classgraph.ScanResult
import io.kotest.core.extensions.Extension
import io.kotest.core.filter.Filter
import io.kotest.core.listeners.Listener
import io.kotest.core.spec.AutoScan
import io.kotest.mpp.instantiate

internal fun loadConfigFromAutoScanInstances(scanResult: ScanResult): DetectedProjectConfig {

   val autoscanned = scanResult
      .getClassesWithAnnotation(AutoScan::class.java.name)
      .map { Class.forName(it.name) }
      .mapNotNull { instantiate(it).getOrNull() }

   val listeners = autoscanned.filterIsInstance<Listener>()
   val filters = autoscanned.filterIsInstance<Filter>()
   val extensions = autoscanned.filterIsInstance<Extension>()

   return DetectedProjectConfig(
      listeners = listeners,
      filters = filters,
      extensions = extensions
   )
}
