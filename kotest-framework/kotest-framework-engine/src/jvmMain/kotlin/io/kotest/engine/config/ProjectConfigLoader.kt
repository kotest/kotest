package io.kotest.engine.config

import io.kotest.common.JVMOnly
import io.kotest.common.KotestInternal
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.log
import io.kotest.engine.instantiateOrObject
import io.kotest.engine.config.PackageUtils
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.reflect.KClass

/**
 * A [ProjectConfigLoader] is responsible for locating concrete implementations
 * of [io.kotest.core.config.AbstractProjectConfig] at runtime based on well-defined
 * fully qualified class names using reflection lookups.
 */
@OptIn(ExperimentalAtomicApi::class)
@KotestInternal
@JVMOnly
object ProjectConfigLoader {

   const val DEFAULT_CONFIG_FQN = "io.kotest.provided.ProjectConfig"

   // we need to disambiguate between no config found (sentinel) and not yet initialized (null)
   private val sentinel = object : AbstractProjectConfig() {}

   // start with not yet initialized (null)
   private val config = AtomicReference<AbstractProjectConfig?>(null)

   /**
    * Returns an [AbstractProjectConfig] instance if one is found on the classpath and loaded by reflection.
    *
    * This will look for a class on the well-defined classpath first, otherwise will search in the spec prefixes.
    */
   fun load(specFqns: Set<String>): AbstractProjectConfig? {
      return config.updateAndGet({ it ?: initialize(specFqns) }).takeUnless { it === sentinel }
   }

   @Suppress("UNCHECKED_CAST")
   private fun initialize(specFqns: Set<String>): AbstractProjectConfig {
      val fqn = fqn()
      log { "Loading project configs from fqn: $fqn" }
      val kclass = runCatching { Class.forName(fqn).kotlin }.getOrNull()
      return if (kclass == null)
         tryFromFqns(specFqns)
      else
         instantiateOrObject(kclass as KClass<out AbstractProjectConfig>).getOrThrow()
   }

   private fun tryFromFqns(specFqns: Set<String>): AbstractProjectConfig {
      if (specFqns.isEmpty()) return sentinel

      val prefix = PackageUtils.commonPrefix(specFqns).removeSuffix(".")
      val packages = PackageUtils.parentPackages(prefix)

      for (pkg in packages) {
         val configFqn = "$pkg.ProjectConfig"
         log { "Searching for project config at $configFqn" }
         val kclass = runCatching { Class.forName(configFqn).kotlin }.getOrNull()
         if (kclass != null) {
            @Suppress("UNCHECKED_CAST")
            return instantiateOrObject(kclass as KClass<out AbstractProjectConfig>).getOrThrow()
         }
      }

      return sentinel
   }

   /**
    * Returns the fully qualified name of the project config class to be loaded, which is
    * either the default or if a sys property is provided, that property's value.
    */
   private fun fqn(): String {
      val fqn = System.getProperty(KotestEngineProperties.PROJECT_CONFIGURATION_FQN)
      return if (fqn == null) {
         log { "No project config class name provided, checking for default at $DEFAULT_CONFIG_FQN" }
         DEFAULT_CONFIG_FQN
      } else fqn
   }
}

