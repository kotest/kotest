package io.kotest.runner.junit.platform.gradle

/**
 * Parses the gradle --tests option when using a nested test.
 *
 * This parser expects a package name, a fully qualified test name, and a test name with context.
 * This parser only checks for nested tests. If the test is not nested, then we can let gradle
 * handle it as normal.
 *
 * Examples:
 *
 * - gradle test --tests "io.kotest.SomeTest.my test -- nested test"
 * - gradle test --tests "io.kotest.SomeTest.my test -- nested test -- leaf test"
 *
 * Examples not handled:
 *
 * - gradle test --tests "io.kotest" (package only)
 * - gradle test --tests "io.kotest.SomeTest" (spec only)
 * - gradle test --tests "io.kotest.SomeTest.my test" (root test)
 *
 */
internal object NestedTestsArgParser {

   // this token is used because gradle thinks of tests as methods, and methods cannot be nested
   // so when we have nested tests we collapse into a fake test name with this delimiter in it.
   // obviously this will break if you have this in a test name, but we use this format everywhere
   const val CONTEXT = " -- "

   // used to replace .* regexes with a value that doesn't contain a period so we can split on the period, then revert
   const val WILDCARD = "__wildcard__"

   /**
    * Parses the --tests arg into a [NestedTestArg] if it references a nested test.
    * If the arg is not for a nested test, then it returns null.
    */
   fun parse(arg: String): NestedTestArg? {
      require(arg.isNotBlank())
      val parts = parts(arg)

      val packageParts = parts.takeWhile { it.first().isLowerCase() }
      val simpleName = parts.dropWhile { it.first().isLowerCase() }.firstOrNull()
      if (simpleName === null) return null

      val testContexts = parts.dropWhile { it.first().isLowerCase() } // drop the package parts
         .drop(1) // drop the class name
         .joinToString(".") // whatever is left is the test name
         .split(CONTEXT) // handle nested tests
         .filter { it.isNotBlank() }

      // if a root test we're not interested in it
      if (testContexts.size < 2) return null

      return NestedTestArg(packageParts.joinToString("."), simpleName, testContexts)
   }

   private fun parts(arg: String): List<String> {
      return arg.removePrefix("\\Q")
         .removeSuffix("\\E")
         .replace("\\E.*\\Q", WILDCARD) // we use a regex wildcard to support periods in names
         .split(".")
         .map { it.replace(WILDCARD, "*") }
   }
}

internal data class NestedTestArg(val packageName: String, val className: String, val contexts: List<String>) {
   init {
      require(contexts.size >= 2) // must be nested
   }
}
