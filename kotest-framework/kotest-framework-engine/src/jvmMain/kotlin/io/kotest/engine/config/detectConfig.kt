package io.kotest.engine.config

import io.github.classgraph.ClassGraph
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.mpp.env
import io.kotest.mpp.sysprop

/**
 * Loads a config object from system properties and by scanning the classpath.
 *
 * Classpath scanning for [AbstractProjectConfig] can be disabled by use of the
 * [KotestEngineProperties.disableConfigurationClassPathScanning] system property.
 */
actual fun detectConfig(): DetectedProjectConfig {

   var config = loadConfigFromSystemProperties()

   if (
      sysprop(KotestEngineProperties.disableConfigurationClassPathScanning) == null ||
      sysprop(KotestEngineProperties.disableAutoScanClassPathScanning) == null
   ) {

      // we scan once for speed and share the results with both config loader and autoscan loader
      classgraph().scan().use { result ->

         if (sysprop(KotestEngineProperties.disableConfigurationClassPathScanning) == null) {
            config = config.merge(loadConfigFromAbstractProjectConfig(result))
         }

         if (sysprop(KotestEngineProperties.disableAutoScanClassPathScanning) == null) {
            config = config.merge(loadConfigFromAutoScanInstances(result))
         }
      }
   }

   return config
}

internal fun classgraph(): ClassGraph {
   return ClassGraph()
      .enableClassInfo()
      .enableExternalClasses()
      .enableAnnotationInfo()
      .ignoreClassVisibility()
      .disableNestedJarScanning()
      .blacklistPackages(
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
         if (env(KotestEngineProperties.disableJarDiscovery) == "true" ||
            sysprop(KotestEngineProperties.disableJarDiscovery) == "true"
         ) {
            disableJarScanning()
         }
      }
}
