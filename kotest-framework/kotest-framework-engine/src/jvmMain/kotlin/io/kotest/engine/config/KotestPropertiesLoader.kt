package io.kotest.engine.config

import io.kotest.common.JVMOnly
import io.kotest.core.log
import io.kotest.engine.KotestEngineProperties
import io.kotest.mpp.syspropOrEnv
import java.util.Properties

/**
 * When you have system properties you want to use for tests launched by both gradle and
 * the intellij plugin, you can place them into a file `kotest.properties` that is located
 * on the classpath (say src/test/resources) and kotest will load those properties and apply them.
 *
 * This is a JVM only feature.
 */
@JVMOnly
internal object KotestPropertiesLoader {

   private const val DefaultKotestPropertiesFilename = "/kotest.properties"

   /**
    * Returns the filename to use for kotest system properties. Allows the filename
    * to be overriden, for example, for different envs.
    */
   private fun systemPropsFilename(): String =
      syspropOrEnv(KotestEngineProperties.propertiesFilename) ?: DefaultKotestPropertiesFilename

   /**
    * Loads system props from the given [filename].
    */
   private fun loadSystemProps(filename: String): Properties {
      val props = Properties()
      val input = object {}::class.java.getResourceAsStream(filename)
      if (input == null) {
         log { "Kotest properties file was not detected" }
         return props
      }
      props.load(input)
      return props
   }

   fun loadAndApplySystemPropsFile() {
      val filename = systemPropsFilename()
      log { "Loading kotest properties from $filename" }
      loadSystemProps(filename).forEach { (key, value) ->
         if (key != null && value != null)
            System.setProperty(key.toString(), value.toString())
      }
   }
}
