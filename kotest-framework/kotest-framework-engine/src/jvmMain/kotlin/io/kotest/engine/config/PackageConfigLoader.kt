package io.kotest.engine.config

import io.kotest.core.config.AbstractPackageConfig
import io.kotest.core.spec.Spec
import io.kotest.engine.instantiateOrObject
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.full.isSubclassOf

internal actual fun loadPackageConfigs(spec: Spec): List<AbstractPackageConfig> = PackageConfigLoader.configs(spec)

/**
 * A [PackageConfigLoader] is responsible for locating concrete implementations of [io.kotest.core.config.AbstractPackageConfig]
 * at runtime, based on a given spec's package, using reflection based lookups.
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

   /**
    * Loads the package configuration class whose expected FQN is derived from [packageConfigName].
    *
    * Behavior:
    * - Returns `null` if the class does not exist.
    * - Returns `null` if the class exists but does not extend `AbstractPackageConfig`.
    * - Returns an `AbstractPackageConfig` if the class exists, extends `AbstractPackageConfig` and can be instantiated.
    * - Throws if the class exists, extends `AbstractPackageConfig` but instantiation fails.
    *
    * @param packageName the package to probe.
    * @return the instantiated `AbstractPackageConfig` or `null` when not present or not a subClass.
    * @throws Throwable if instantiation of a valid subtype fails.
    */
   private fun loadPackageConfig(packageName: String): AbstractPackageConfig? {
      val kClass = runCatching { Class.forName(packageConfigName(packageName)).kotlin }.getOrNull()
         ?.takeIf { it.isSubclassOf(AbstractPackageConfig::class) }
         ?: return null

      return instantiateOrObject(kClass).getOrThrow() as AbstractPackageConfig
   }

   private fun packageConfigName(packageName: String) = "$packageName.PackageConfig"
}
