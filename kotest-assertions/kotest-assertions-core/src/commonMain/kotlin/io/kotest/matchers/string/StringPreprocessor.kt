package io.kotest.matchers.string

import io.kotest.assertions.AssertionsConfig.mapFileEndingsToUnix

interface StringPreprocessor {
   fun map(input: String): String

   companion object {
      fun process(input: CharSequence) = default.fold(input) { acc, op -> op.map(acc.toString()) }
      fun process(input: String) = default.fold(input) { acc, op -> op.map(acc) }

      val default: List<StringPreprocessor> =
         if (mapFileEndingsToUnix.value) listOf(MapFileEndingsToUnixStringPreprocessor) else emptyList()
   }
}

object MapFileEndingsToUnixStringPreprocessor : StringPreprocessor {
   override fun map(input: String): String {
      return input.replace("\r\n?".toRegex(), "\n")
   }
}


