package io.kotest.engine.extensions

/**
 * Parses an env variable of the form some.package.Class.test
 *
 * This parser expects a package name, a class name, and a test name.
 * The package is required, the class is optional, and the test is optional.
 * The test can be a nested test.
 *
 * Package names are expected to be lower case.
 * The class name is expected to be upper case.
 * The test context is whatever comes after the class name.
 *
 * Examples:
 *
 * - io.kotest (package only)
 * - io.kotest.SomeTest (spec only)
 * - io.kotest.SomeTest.my test (root test)
 * - io.kotest.SomeTest.my test -- nested test
 *
 */
internal object TestPatternParser {

   // this token is used to delimit nested tests
   const val CONTEXT = " -- "

   fun parse(arg: String): TestPattern {
      require(arg.isNotBlank())
      val parts = arg.split(".")

      val packageParts = parts.takeWhile { it.first().isLowerCase() }
      val simpleName = parts.dropWhile { it.first().isLowerCase() }.firstOrNull()
      if (simpleName === null) return TestPattern(packageParts.joinToString("."), false, null, emptyList())
      if (simpleName == "*") return TestPattern(packageParts.joinToString("."), true, null, emptyList())

      val test = parts.dropWhile { it.first().isLowerCase() } // drop the package parts
         .drop(1) // drop the class name
         .firstOrNull()

      if (test == null) return TestPattern(packageParts.joinToString("."), false, simpleName, emptyList())

      val contexts = test.split(CONTEXT).filter { it.isNotBlank() }
      return TestPattern(packageParts.joinToString("."), false, simpleName, contexts)
   }
}

internal data class TestPattern(
   val packageName: String,
   val subpackages: Boolean,
   val className: String?,
   val contexts: List<String>
)
