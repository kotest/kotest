package io.kotest.assertions

import io.kotest.common.sysprop
import io.kotest.common.syspropOrEnv

object AssertionsConfigSystemProperties {
   const val DISABLE_NAN_NEQUALITY = "kotest.assertions.nan.equality.disable"
   const val COLLECTIONS_PRINT_SIZE = "kotest.assertions.collection.print.size"
   const val MAP_FILE_ENDINGS_TO_UNIX = "kotest.assertions.string.map.file.endings.unix"
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

   val mapFileEndingsToUnix: EnvironmentConfigValue<Boolean> =
      EnvironmentConfigValue(AssertionsConfigSystemProperties.MAP_FILE_ENDINGS_TO_UNIX, false, String::toBoolean)

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
   val name: String,
   var value: T,
) {

   companion object {
      operator fun <T> invoke(name: String, defaultValue: T, converter: (String) -> T): EnvironmentConfigValue<T> {
         val loaded = syspropOrEnv(name)?.let { converter(it) } ?: defaultValue
         try {
            return EnvironmentConfigValue(name, loaded)
         } catch (e: Exception) {
            throw KotestConfigurationException("Could not load sysprop or envvar from $name: $e", e)
         }
      }
   }

   /**
    * Executes the given [thunk] with the value of this [io.kotest.assertions.EnvironmentConfigValue] set
    * to the [newValue] and then resets afterward. Not concurrent safe. Designed for testing.
    */
   fun withValue(newValue: T, thunk: () -> Unit) {
      val oldValue = value
      value = newValue
      thunk()
      value = oldValue
   }
}

class KotestConfigurationException(message: String, cause: Throwable?) : RuntimeException(message, cause)
