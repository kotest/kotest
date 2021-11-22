package io.kotest.engine.interceptors

import io.kotest.common.JVMOnly
import io.kotest.core.internal.KotestEngineProperties
import io.kotest.engine.EngineResult
import io.kotest.mpp.log
import java.util.Properties

/**
 * When you have system properties you want to use for tests launched by both gradle and
 * the intellij plugin, you can place them into a file `kotest.properties` that is located
 * on the classpath (say src/test/resources) and kotest will load those properties and apply them.
 *
 * This is a JVM only extension.
 */
@JVMOnly
internal object KotestPropertiesInterceptor : EngineInterceptor {

   private const val DefaultKotestPropertiesFilename = "/kotest.properties"

   /**
    * Returns the filename to use for kotest system properties. Allows the filename
    * to be overridden, for example, for different envs.
    */
   private fun systemPropsFilename(): String =
      System.getProperty(KotestEngineProperties.propertiesFilename) ?: DefaultKotestPropertiesFilename

   /**
    * Loads system props from the given [filename].
    */
   private fun loadSystemProps(filename: String): Properties {
      val props = Properties()
      val input = object {}::class.java.getResourceAsStream(filename) ?: return props
      props.load(input)
      return props
   }

   private fun loadAndApplySystemProps() {
      val filename = systemPropsFilename()
      log { "Loading kotest properties from $filename" }
      loadSystemProps(filename).forEach { (key, value) ->
         if (key != null && value != null)
            System.setProperty(key.toString(), value.toString())
      }
   }

   override suspend fun intercept(
      context: EngineContext,
      execute: suspend (EngineContext) -> EngineResult
   ): EngineResult {
      loadAndApplySystemProps()
      return execute(context)
   }
}
