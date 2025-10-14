package io.kotest.assertions

import io.kotest.common.sysprop
import io.kotest.common.syspropOrEnv

object AssertionsConfigSystemProperties {
   const val DISABLE_NAN_NEQUALITY = "kotest.assertions.nan.equality.disable"
   @Deprecated("Use correct spelling")
   const val DISABLE_NA_NEQUALITY = DISABLE_NAN_NEQUALITY
   const val COLLECTIONS_PRINT_SIZE = "kotest.assertions.collection.print.size"
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
      get() = sysprop(AssertionsConfigSystemProperties.DISABLE_NAN_NEQUALITY)?.toBoolean() ?: false

   val maxCollectionPrintSize: EnvironmentConfigValue<Int> =
      EnvironmentConfigValue(AssertionsConfigSystemProperties.COLLECTIONS_PRINT_SIZE, 20, String::toInt)

   val maxSimilarityPrintSize: EnvironmentConfigValue<Int> =
      EnvironmentConfigValue("kotest.assertions.similarity.print.size", 5, String::toInt)

   val similarityThresholdInPercent: EnvironmentConfigValue<Int> =
      EnvironmentConfigValue("kotest.assertions.similarity.thresholdInPercent", 50, String::toInt)

   val similarityThresholdInPercentForStrings: EnvironmentConfigValue<Int> =
      EnvironmentConfigValue("kotest.assertions.similarity.thresholdInPercentForStrings", 66, String::toInt)

   val minSubtringSubmatchingSize: EnvironmentConfigValue<Int> =
      EnvironmentConfigValue("kotest.assertions.string.submatching.min.substring.size", 8, String::toInt)

   val maxSubtringSubmatchingSize: EnvironmentConfigValue<Int> =
      EnvironmentConfigValue("kotest.assertions.string.submatching.max.substring.size", 1024, String::toInt)

   val minValueSubmatchingSize: EnvironmentConfigValue<Int> =
      EnvironmentConfigValue("kotest.assertions.string.submatching.min.value.size", 8, String::toInt)

   val maxValueSubmatchingSize: EnvironmentConfigValue<Int> =
      EnvironmentConfigValue("kotest.assertions.string.submatching.max.value.size", 1024, String::toInt)

   val enabledSubmatchesInStrings: EnvironmentConfigValue<Boolean> =
      EnvironmentConfigValue("kotest.assertions.string.submatching.enabled", true, String::toBoolean)

}

class EnvironmentConfigValue<T>(
   private val name: String,
   private val defaultValue: T,
   val converter: (String) -> T
) {

   val value: T = loadValue()

   private fun loadValue(): T {
      val loaded = syspropOrEnv(name) ?: return defaultValue
      try {
         return converter(loaded)
      } catch (e: Exception) {
         throw KotestConfigurationException("Could not load sysprop or envvar from $name: $e", e)
      }
   }
}

class KotestConfigurationException(message: String, cause: Throwable?) : RuntimeException(message, cause)
