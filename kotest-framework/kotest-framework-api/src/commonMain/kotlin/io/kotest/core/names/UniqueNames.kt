package io.kotest.core.names

object UniqueNames {

   /**
    * Returns a unique name for the given name and current test names.
    */
   fun uniqueTestName(name: String, testNames: Set<String>): String? {
      if (!testNames.contains(name)) return null
      var n = 1
      fun nextName() = "($n) $name"
      while (testNames.contains(nextName()))
         n++
      return nextName()
   }
}
