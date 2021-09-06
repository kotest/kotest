package io.kotest.engine.config

import io.github.classgraph.ClassGraph
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.mpp.env
import io.kotest.mpp.sysprop

/**
 * Creates a [ClassGraph] builder for scanning configs.
 */
internal fun classgraph(): ClassGraph {
   return ClassGraph()
      .enableClassInfo()
      .enableExternalClasses()
      .enableAnnotationInfo()
      .ignoreClassVisibility()
      .disableNestedJarScanning()
      // do not change this to use rejectPackages as then it will fail in builds
      // using an older version of classgraph
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
