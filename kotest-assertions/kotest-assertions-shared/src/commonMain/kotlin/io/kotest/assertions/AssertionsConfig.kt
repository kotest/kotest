package io.kotest.assertions

import io.kotest.mpp.sysprop

object AssertionsConfig {

   val showDataClassDiff: Boolean
      get() = sysprop("kotest.assertions.show-data-class-diffs", true)

   val largeStringDiffMinSize: Int
      get() = sysprop("kotest.assertions.multi-line-diff-size", 50)

   val multiLineDiff: String
      get() = sysprop("kotest.assertions.multi-line-diff", "")

   val maxErrorsOutput: Int
      get() = sysprop("kotest.assertions.output.max")?.toIntOrNull() ?: 10

   val maxCollectionEnumerateSize: Int
      get() = sysprop("kotest.assertions.collection.enumerate.size")?.toIntOrNull() ?: 20

   val maxCollectionPrintSize: Configurable<Int> = Configurable<Int>("kotest.assertions.collection.print.size", 20, String::toInt)
}

class Configurable<T>(
   private val name: String,
   val defaultValue: T,
   val converter: (String) -> T
) {
   val sourceDescription: String = ConfigurationLoader.getSourceDescription(name)
   val value: T = loadValue()

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
   fun getSourceDescription(name: String): String
}

class KotestConfigurationException(message: String, cause: Throwable?) : RuntimeException(message, cause)
