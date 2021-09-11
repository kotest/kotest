package io.kotest.engine.test

import io.kotest.core.test.DescriptionName
import io.kotest.core.test.DuplicateTestNameMode
import io.kotest.core.test.Identifiers

/**
 * Tracks test names for a context, and reports on duplicates, or modifies names
 * to be unique based on the value of the given [DuplicateTestNameMode].
 */
internal class DuplicateTestNameHandler(private val mode: DuplicateTestNameMode) {

   private val names = mutableSetOf<String>()

   private fun message(name: String): String =
      "Duplicated test name ${name}. To disable this message, set DuplicateTestNameMode to None."

   fun handle(name: DescriptionName.TestName): String? {
      val isUnique = names.add(name.name)
      if (isUnique) return null
      return when (mode) {
         DuplicateTestNameMode.Error -> throw DuplicateTestNameException(message(name.name))
         DuplicateTestNameMode.Silent -> makeUniqueName(name.name)
         DuplicateTestNameMode.Warn -> {
            println("WARN: " + message(name.name))
            makeUniqueName(name.name)
         }
      }
   }

   private fun makeUniqueName(name: String): String {
      val unique = Identifiers.uniqueTestName(name, names)
      names.add(unique)
      return unique
   }
}

class DuplicateTestNameException(message: String) : RuntimeException(message)
