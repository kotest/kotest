package io.kotest.matchers.string

import io.kotest.assertions.AssertionsConfig.mapFileEndingsToUnix

interface StringPreprocessor {
   fun map(input: String): String

   companion object {
      fun process(input: CharSequence) = process(input.toString())
      fun process(input: String) = processors().fold(input) { acc, op -> op.map(acc) }

      private fun processors(): List<StringPreprocessor> =
         if (mapFileEndingsToUnix.value) listOf(MapFileEndingsToUnixStringPreprocessor) else emptyList()
   }
}

object MapFileEndingsToUnixStringPreprocessor : StringPreprocessor {
   override fun map(input: String): String {
      return input.replace("\r\n?".toRegex(), "\n")
   }
}


