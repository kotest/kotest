package io.kotest.engine.gradle

import io.kotest.common.KotestInternal

/**
 * Parses the gradle --tests option into Kotest compatible selectors when invoked by the intelli plugin.
 *
 * This parser expects a package name, a fully qualified test name, or a fully qualified test name with context.
 * Wildcards are not part of the specification.
 *
 * The plugin will prefix the package name with kotest_intellij_plugin. to indicate that it
 * is being invoked by the plugin.
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
object TestArgParser {

   // this token is used because gradle thinks of tests as methods, and methods cannot be nested
   // so when we have nested tests we collapse into a fake test name with this delimiter in it.
   const val CONTEXT = "__context__"

   const val MAGIC_MARKER = "kotest_intellij_plugin."

   const val PERIOD = "__period__"

   fun parse(arg: String): TestArg? {
      require(arg.isNotBlank())
      if (!arg.removePrefix("\\Q").startsWith(MAGIC_MARKER)) return null

      val parts = arg
         .removePrefix("\\Q")
         .removePrefix(MAGIC_MARKER)
         .removeSuffix("\\E")
         .replace("\\E.*\\Q", PERIOD) // we use a regex wildcard to support periods in names
         .split(".")
         .map { it.replace(PERIOD, ".") }

      val packageParts = parts.takeWhile { it.first().isLowerCase() }
      val simpleName = parts.dropWhile { it.first().isLowerCase() }.firstOrNull()
      val testContexts = parts.dropWhile { it.first().isLowerCase() }
         .drop(1)
         .joinToString(".")
         .split(CONTEXT)
         .filter { it.isNotBlank() }

      val fqn = (packageParts + listOfNotNull(simpleName)).joinToString(".")

      return if (simpleName == null) {
         TestArg.Package(packageParts.joinToString("."))
      } else if (testContexts.isEmpty()) {
         TestArg.Class(fqn)
      } else {
         TestArg.Test(fqn, testContexts)
      }
   }
}

sealed interface TestArg {

   // will match a package plus any subpackages
   data class Package(val packageName: String) : TestArg

   // will match an exact class by fully qualified name
   data class Class(val fqn: String) : TestArg

   // will match a test path including child tests, so if the contexts are ["a", "b"] then it
   // would match tests a -- b, as well as a -- b -- c, but not a -- d
   data class Test(val fqn: String, val contexts: List<String>) : TestArg
}
