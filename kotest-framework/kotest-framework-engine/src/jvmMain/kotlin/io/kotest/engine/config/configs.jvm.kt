package io.kotest.engine.config

import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.spec.Spec
import io.kotest.engine.config.PackageConfigLoader.loadPackageConfig
import io.kotest.engine.instantiateOrObject

internal actual fun loadPackageConfigs(spec: Spec): List<AbstractPackageConfig> = PackageConfigLoader.configs(spec)

/**
 * A [PackageConfigLoader] is responsible for locating concrete implementations of [AbstractPackageConfig]
 * at runtime, based on a given spec's package.
 *
 * On the JVM this will use [loadPackageConfig] to locate the config using reflection based on package names.
 * On other platforms, this will return an empty list.
 */
internal object PackageConfigLoader {

   // we don't need to re-run reflection for the same packages
   private val cache = mutableMapOf<String, AbstractPackageConfig?>()

   fun configs(spec: Spec): List<AbstractPackageConfig> {
      val fqn = spec::class.qualifiedName ?: return emptyList()
      if (!fqn.contains(".")) return emptyList()
      val packageName = fqn.substringBeforeLast(".")
      return configs(packageName)
   }

   private fun configs(packageName: String): List<AbstractPackageConfig> {
      val config = cache.getOrPut(packageName) { loadPackageConfig(packageName) }
      val configs = listOfNotNull(config)
      if (packageName.contains(".")) {
         val parentPackageName = packageName.substringBeforeLast(".")
         return configs + configs(parentPackageName)
      } else return configs
   }

   private fun loadPackageConfig(packageName: String): AbstractPackageConfig? {
      // ok to skip if the class doesn't exist
      val kclass = runCatching { Class.forName(packageConfigName(packageName)).kotlin }.getOrNull() ?: return null
      // but should fail if the class exists but cannot be instantiated
      return instantiateOrObject(kclass).getOrThrow() as AbstractPackageConfig
   }

   private fun packageConfigName(packageName: String) = "$packageName.PackageConfig"
}

