package io.kotest.engine.test.names

import io.kotest.core.plan.TestName
import io.kotest.core.test.DuplicateTestNameMode
import io.kotest.core.test.Identifiers

/**
 * Tracks test names and handles duplicate names.
 *
 * The action to take on duplicates is determined by the [DuplicateTestNameMode] parameter.
 */
class DuplicateTestNameHandler(private val mode: DuplicateTestNameMode) {

   private val names = mutableSetOf<String>()

   private fun message(name: String): String =
      "Duplicated test name ${name}. To disable this message, set ${DuplicateTestNameMode::class.simpleName} to None."

   fun handle(name: TestName): String? {
      val isUnique = names.add(name.testName)
      if (isUnique) return null
      return when (mode) {
         DuplicateTestNameMode.Error -> error(message(name.testName))
         DuplicateTestNameMode.Silent -> makeUniqueName(name.testName)
         DuplicateTestNameMode.Warn -> {
            println("WARN: " + message(name.testName))
            makeUniqueName(name.testName)
         }
      }
   }

   private fun makeUniqueName(name: String): String {
      val unique = Identifiers.uniqueTestName(name, names)
      names.add(unique)
      return unique
   }
}
