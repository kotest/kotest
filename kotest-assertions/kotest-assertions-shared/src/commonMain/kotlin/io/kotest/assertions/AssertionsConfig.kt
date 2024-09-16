package io.kotest.assertions

import io.kotest.mpp.sysprop

object AssertionsConfigSystemProperties {
   const val disableNaNEquality = "kotest.assertions.nan.equality.disable"
}

object AssertionsConfig {

   val showDataClassDiff: Boolean
      get() = sysprop("kotest.assertions.show-data-class-diffs", true)

   val largeStringDiffMinSize: Int
      get() = sysprop("kotest.assertions.multi-line-diff-size", 50)

   val multiLineDiff: String
      get() = sysprop("kotest.assertions.multi-line-diff", "")

   val maxErrorsOutput: Int
      get() = sysprop("kotest.assertions.output.max")?.toIntOrNull() ?: 10

   val mapDiffLimit: Int
      get() = sysprop("kotest.assertions.map.diff.limit", 100)

   val maxCollectionEnumerateSize: Int
      get() = sysprop("kotest.assertions.collection.enumerate.size")?.toIntOrNull() ?: 20

   val disableNaNEquality: Boolean
      get() = sysprop(AssertionsConfigSystemProperties.disableNaNEquality)?.toBoolean() ?: false

   val maxCollectionPrintSize: ConfigValue<Int> =
      EnvironmentConfigValue<Int>("kotest.assertions.collection.print.size", 20, String::toInt)

   val maxSimilarityPrintSize: ConfigValue<Int> =
      EnvironmentConfigValue<Int>("kotest.assertions.similarity.print.size", 5, String::toInt)

   val similarityThresholdInPercent: ConfigValue<Int> =
      EnvironmentConfigValue<Int>("kotest.assertions.similarity.thresholdInPercent", 50, String::toInt)
}

interface ConfigValue<T> {
   val sourceDescription: String?
   val value: T
}

class EnvironmentConfigValue<T>(
   private val name: String,
   private val defaultValue: T,
   val converter: (String) -> T
) : ConfigValue<T> {
   override val sourceDescription: String? = ConfigurationLoader.getSourceDescription(name)
   override val value: T = loadValue()

   private fun loadValue(): T {
      val loaded = ConfigurationLoader.getValue(name) ?: return defaultValue

      try {
         return converter(loaded)
      } catch (e: Exception) {
         throw KotestConfigurationException("Could not load value from $sourceDescription: $e", e)
      }
   }
}

internal expect object ConfigurationLoader {
   fun getValue(name: String): String?
   fun getSourceDescription(name: String): String?
}

class KotestConfigurationException(message: String, cause: Throwable?) : RuntimeException(message, cause)
