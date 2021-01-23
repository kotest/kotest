package io.kotest.engine.config

import io.kotest.core.internal.KotestEngineSystemProperties
import java.util.Properties

const val KotestPropertiesFilename = "kotest.properties"

/**
 * When you have system properties you want to use for tests launched by both gradle and
 * the intellij plugin, you can place them into a file `kotest.properties` that is located
 * on the classpath (say src/test/resources) and kotest will load those properties and apply them.
 */
fun loadAndApplySystemProps() {
   loadSystemProps().forEach { (key, value) ->
      if (key != null && value != null)
         System.setProperty(key.toString(), value.toString())
   }
}

fun systemPropsFilename() =
   System.getProperty(KotestEngineSystemProperties.propertiesFilename) ?: KotestPropertiesFilename

internal fun loadSystemProps(): Properties {
   val props = Properties()
   val input = object {}::class.java.getResourceAsStream("/" + systemPropsFilename()) ?: return props
   props.load(input)
   return props
}
