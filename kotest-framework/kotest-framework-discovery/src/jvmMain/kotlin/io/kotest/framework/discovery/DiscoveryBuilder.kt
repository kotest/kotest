package io.kotest.framework.discovery

import io.github.classgraph.ClassGraph

data class DiscoveryBuilder(
   val jarScanning: Boolean,
   val nestedJarScanning: Boolean,
   val externalClasses: Boolean,
   val ignoreClassVisibility: Boolean,
   val blacklistPackages: Set<String>,
) {

   companion object {
      fun builder(): DiscoveryBuilder = DiscoveryBuilder(
         jarScanning = false,
         nestedJarScanning = false,
         externalClasses = true,
         ignoreClassVisibility = true,
         blacklistPackages = emptySet(),
      )
   }

   fun withExternalClasses(externalClasses: Boolean): DiscoveryBuilder {
      return copy(externalClasses = externalClasses)
   }

   fun withIgnoreClassVisibility(ignoreClassVisibility: Boolean): DiscoveryBuilder {
      return copy(ignoreClassVisibility = ignoreClassVisibility)
   }

   fun withJarScanning(jarScanning: Boolean): DiscoveryBuilder {
      return copy(jarScanning = jarScanning)
   }

   fun withNestedJarScanning(nestedJarScanning: Boolean): DiscoveryBuilder {
      return copy(nestedJarScanning = nestedJarScanning)
   }

   fun addDefaultBlacklistPackages(): DiscoveryBuilder {
      return addBlacklistPackages(
         "java.*",
         "javax.*",
         "sun.*",
         "com.sun.*",
         "kotlin.*",
         "kotlinx.*",
         "androidx.*",
         "org.jetbrains.kotlin.*",
         "org.junit.*"
      )
   }

   fun addBlacklistPackages(vararg packages: String): DiscoveryBuilder {
      return copy(blacklistPackages = blacklistPackages + packages)
   }

   fun build(): Discovery {
      return Discovery(classgraph = classgraph())
   }

   private fun classgraph(): ClassGraph {
      val cg = ClassGraph().enableClassInfo()
      if (ignoreClassVisibility) cg.enableExternalClasses()
      if (!jarScanning) cg.disableJarScanning()
      if (!nestedJarScanning) cg.disableNestedJarScanning()
      cg.blacklistPackages(*blacklistPackages.toTypedArray())
      return cg
   }
}
