package io.kotest.engine.test.names

import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestName
import io.kotest.core.names.UniqueNames

/**
 * Tracks test names for a context, and reports on duplicates, or modifies names
 * to be unique based on the value of the given [DuplicateTestNameMode].
 */
internal class DuplicateTestNameHandler(private val mode: DuplicateTestNameMode) {

   private val names = mutableSetOf<String>()

   private fun message(name: String): String =
      "Duplicated test name ${name}. To disable this message, set DuplicateTestNameMode to None."

   /**
    * Returns a unique name or null if the name is already unique.
    */
   fun handle(name: TestName): String? {
      val isUnique = names.add(name.testName)
      if (isUnique) return null
      return when (mode) {
         DuplicateTestNameMode.Error -> throw DuplicateTestNameException(message(name.testName))
         DuplicateTestNameMode.Silent -> makeUniqueName(name.testName)
         DuplicateTestNameMode.Warn -> {
            println("WARN: " + message(name.testName))
            makeUniqueName(name.testName)
         }
      }
   }

   private fun makeUniqueName(name: String): String {
      val unique = UniqueNames.uniqueTestName(name, names) ?: name
      names.add(unique)
      return unique
   }
}

class DuplicateTestNameException(message: String) : RuntimeException(message)
