package io.kotest.engine.config

import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.spec.Spec

/**
 * Returns the [AbstractPackageConfig] for the given package name, or null if one cannot be found.
 *
 * This is a JVM only function. On other platforms, will return null.
 */
internal expect fun loadPackageConfig(packageName: String): AbstractPackageConfig?

/**
 * A [PackageConfigLoader] is responsible for locating concrete implementations of [AbstractPackageConfig]
 * at runtime, based on a given spec's package.
 *
 * On the JVM this will use [loadPackageConfig] to locate the config using reflection based on package names.
 * On other platforms, this will return an empty list.
 */
object PackageConfigLoader {

   // we don't need to re-run reflection for the same packages
   private val cache = mutableMapOf<String, AbstractPackageConfig?>()

   fun configs(spec: Spec): List<AbstractPackageConfig> {
      val fqn = spec::class.qualifiedName ?: return emptyList()
      if (!fqn.contains(".")) return emptyList()
      val packageName = fqn.substringBeforeLast(".")
      return configs(packageName)
   }

   fun configs(packageName: String): List<AbstractPackageConfig> {
      val config = cache.getOrPut(packageName) { loadPackageConfig(packageName) }
      val configs = listOfNotNull(config)
      if (packageName.contains(".")) {
         val parentPackageName = packageName.substringBeforeLast(".")
         return configs + configs(parentPackageName)
      } else return configs
   }
}
