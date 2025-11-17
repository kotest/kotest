package io.kotest.engine.gradle

import io.kotest.common.KotestInternal

/**
 * Parses the gradle --tests option into Kotest compatible selectors when invoked by the intelli plugin.
 *
 * This parser expects a package name, a fully qualified test name, or a fully qualified test name with context.
 *
 * Examples:
 *
 * - gradle test --tests "io.kotest"
 * - gradle test --tests "io.kotest.SomeTest"
 * - gradle test --tests "io.kotest.SomeTest.my test"
 * - gradle test --tests "io.kotest.SomeTest.my test__context__nested test"
 *
 */
@KotestInternal
object TestFilterParser {

   // this token is used because gradle thinks of tests as methods, and methods cannot be nested
   // so when we have nested tests we collapse into a fake test name with this delimiter in it.
   const val CONTEXT = "__context__"

   // used to replace .* regexes with a value that doesn't contain a period so we can split on the period, then revert
   const val WILDCARD = "__wildcard__"

   /**
    * Returns true if the given [arg] is a test filter that includes a test name part.
    *
    * This function is not particularly clever, it supports the simple cases that the kotest plugin requires,
    * it does not support fancy prefix matching etc.
    */
   fun isTestFilter(arg: String): Boolean {
      val parts = parts(arg)

      // We consider the filter to contain a test if, after finding the first class name, there is another part after
      // The Gradle spec says that a class name is just the first part in title case
      // This means lower case class names, etc, are going to break that logic

      // drop all package parts, then drop the class part, then whatever is left is the test name
      return parts.dropWhile { it.first().isLowerCase() }.drop(1).isNotEmpty()
   }

   fun parse(arg: String): TestFilter {
      require(arg.isNotBlank())
      val parts = parts(arg)

      val packageParts = parts.takeWhile { it.first().isLowerCase() }
      val simpleName = parts.dropWhile { it.first().isLowerCase() }.firstOrNull()
      val testContexts = parts.dropWhile { it.first().isLowerCase() } // drop the package parts
         .drop(1) // drop the class name
         .joinToString(".") // whatever is left is the test name
         .split(CONTEXT) // handle nested tests
         .filter { it.isNotBlank() }

      val fqn = (packageParts + listOfNotNull(simpleName)).joinToString(".")
      return TestFilter(fqn, testContexts)
   }

   private fun parts(arg: String): List<String> {
      return arg.removePrefix("\\Q")
         .removeSuffix("\\E")
         .replace("\\E.*\\Q", WILDCARD) // we use a regex wildcard to support periods in names
         .split(".")
         .map { it.replace(WILDCARD, "*") }
   }
}


// will match a test path including child tests, so if the contexts are ["a", "b"] then it
// would match tests a, a -- b, as a -- b -- c, but not d, or a -- e
data class TestFilter(val fqn: String, val contexts: List<String>)
