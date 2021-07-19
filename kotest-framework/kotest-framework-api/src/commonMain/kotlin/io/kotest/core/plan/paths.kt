package io.kotest.core.plan

/**
 * A parsable, consistent path to a [TestCase].
 *
 * A path is made up of one or more components.
 *
 * For example, the following fun spec test:
 *
 * context("my context") {
 *   context("nested context") {
 *     test("my test") { }
 *   }
 * }
 *
 * Would be represented as a test path like:
 *
 * my context -- nested context -- my test
 */
data class TestPath(val value: String) {

   companion object {
      const val PathSeparator = " -- "
      operator fun invoke(names: List<String>): TestPath {
         return TestPath(names.joinToString(PathSeparator))
      }
   }

   fun prepend(name: String) = TestPath("$name$PathSeparator$value")
   fun append(name: String) = TestPath("$value$PathSeparator$name")

   /**
    * Returns true if this descriptor is ancestor (1..nth-parent) of the given [path].
    */
   fun isAncestorOf(path: TestPath): Boolean = path.value.startsWith(this.value)
}

