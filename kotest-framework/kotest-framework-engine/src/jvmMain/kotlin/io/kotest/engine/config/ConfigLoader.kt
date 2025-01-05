package io.kotest.engine.config

import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.spec.Spec
import io.kotest.engine.instantiateOrObject
import java.util.concurrent.ConcurrentHashMap

/**
 * A [ConfigLoader] is responsible for locating concrete implementations of [AbstractPackageConfig]
 * at runtime, based on a given spec's package.
 */
class ConfigLoader {

   // we don't need to re-run reflection for the same packages
   private val cache = ConcurrentHashMap<String, AbstractPackageConfig?>()

   fun configs(spec: Spec): List<AbstractPackageConfig> {
      val packages = parents(spec::class.java.`package`.name) + spec::class.java.`package`.name
      return packages.mapNotNull { cache.getOrPut(it) { detect(it) } }
   }

   private fun detect(packageName: String): AbstractPackageConfig? {
      // ok to skip if the class doesn't exist
      val kclass = runCatching { Class.forName("$packageName.KotestPackageConfig").kotlin }.getOrNull() ?: return null
      // but should fail if the class exists but cannot be instantiated
      return instantiateOrObject(kclass).getOrThrow() as AbstractPackageConfig
   }

   // returns all the parent package names for this package
   private fun parents(packageName: String): List<String> {
      return packageName.split(".").fold(emptyList<String>()) { acc, op ->
         if (acc.isEmpty()) listOf(op)
         else acc + (acc.last() + ".$op")
      }
   }
}
