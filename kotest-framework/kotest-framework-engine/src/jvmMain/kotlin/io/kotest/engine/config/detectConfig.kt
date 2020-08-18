package io.kotest.engine.config

import io.github.classgraph.ClassGraph
import io.kotest.core.internal.KotestEngineSystemProperties
import io.kotest.mpp.sysprop

/**
 * Loads a config object from system properties and by scanning the classpath.
 *
 * Classpath scanning for [AbstractProjectConfig] can be disabled by use of the
 * [KotestEngineSystemProperties.disableConfigurationClassPathScanning] system property.
 */
actual fun detectConfig(): DetectedProjectConfig {

   var config = loadConfigFromSystemProperties()

   if (
      sysprop(KotestEngineSystemProperties.disableConfigurationClassPathScanning) == null ||
      sysprop(KotestEngineSystemProperties.disableAutoScanClassPathScanning) == null
   ) {

      // we scan once for speed and share the results with both config loader and autoscan loader
      val scanResult = ClassGraph()
         .enableClassInfo()
         .enableAnnotationInfo()
         .enableExternalClasses()
         .disableNestedJarScanning()
         .rejectPackages("java.*", "javax.*", "sun.*", "com.sun.*", "kotlin.*", "kotlinx.*", "android.*")
         .scan()

      if (sysprop(KotestEngineSystemProperties.disableConfigurationClassPathScanning) == null) {
         config = config.merge(loadConfigFromAbstractProjectConfig(scanResult))
      }

      if (sysprop(KotestEngineSystemProperties.disableAutoScanClassPathScanning) == null) {
         config = config.merge(loadConfigFromAutoScanInstances(scanResult))
      }
   }

   return config
}
