package io.kotest.engine.config

import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.spec.Spec
import io.kotest.engine.config.PackageConfigLoader.loadPackageConfig
import io.kotest.engine.instantiateOrObject
import java.util.concurrent.ConcurrentHashMap

/**
 * A [PackageConfigLoader] is responsible for locating concrete implementations of [io.kotest.core.config.AbstractPackageConfig]
 * at runtime, based on a given spec's package.
 *
 * On the JVM this will use [loadPackageConfig] to locate the config using reflection based on package names.
 * On other platforms, this will return an empty list.
 */
internal object PackageConfigLoader {

   sealed interface CachedConfig {

      val config: AbstractPackageConfig?

      object Null : CachedConfig {
         override val config: AbstractPackageConfig? = null
      }

      data class Config(override val config: AbstractPackageConfig) : CachedConfig
   }

   // we don't need to re-run reflection for the same packages
   // ConcurrentHashMap doesn't allow null values, so we use a special value to indicate null
   internal val cache = ConcurrentHashMap<String, CachedConfig>()

   fun configs(spec: Spec): List<AbstractPackageConfig> {
      return configs(spec::class.java.`package`.name)
   }

   internal fun configs(packageName: String): List<AbstractPackageConfig> {
      return packages(packageName).mapNotNull { cachedPackageConfig(it) }
   }

   private fun cachedPackageConfig(packageName: String): AbstractPackageConfig? {
      return cache.getOrPut(packageName) {
         val config = loadPackageConfig(packageName)
         if (config == null) CachedConfig.Null else CachedConfig.Config(config)
      }.config
   }

   internal fun packages(packageName: String): List<String> {
      return if (packageName.contains('.'))
         listOf(packageName) + packages(packageName.substringBeforeLast("."))
      else
         listOf(packageName)
   }

   private fun loadPackageConfig(packageName: String): AbstractPackageConfig? {
      // ok to skip if the class doesn't exist
      val kclass = runCatching { Class.forName(packageConfigName(packageName)).kotlin }.getOrNull() ?: return null
      // but should fail if the class exists but cannot be instantiated
      return instantiateOrObject(kclass).getOrThrow() as AbstractPackageConfig
   }

   private fun packageConfigName(packageName: String) = "$packageName.PackageConfig"
}
