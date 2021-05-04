package io.kotest.engine.test

import io.kotest.core.test.DuplicateTestNameMode
import io.kotest.core.test.Identifiers
import io.kotest.core.test.TestCase

/**
 * A handler for test names. Will track all test names used for a particular context.
 */
class DuplicateTestNameHandler(private val mode: DuplicateTestNameMode) {

   private val names = mutableSetOf<String>()

   private fun message(name: String): String =
      "Duplicated test name ${name}. To disable this message, set DuplicateTestNameMode to None."

   fun handle(testCase: TestCase): String? {
      val isUnique = names.add(testCase.description.name.name)
      if (isUnique) return null
      return when (mode) {
         DuplicateTestNameMode.Error -> error(message(testCase.displayName))
         DuplicateTestNameMode.None -> makeUniqueName(testCase)
         DuplicateTestNameMode.Warn -> {
            println("WARN: " + message(testCase.displayName))
            makeUniqueName(testCase)
         }
      }
   }

   private fun makeUniqueName(testCase: TestCase): String {
      val unique = Identifiers.uniqueTestName(testCase.description.name.name, names)
      names.add(unique)
      return unique
   }
}
