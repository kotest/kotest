package io.kotlintest

/**
 * A [Scope] is a block of code that acts as a node in the test plan tree.
 * A scope can either be a leaf level [TestCase] - which you can think of as a
 * unit test - and branch level [TestContainer]s. Containers can nest
 * further containers - the actual structure of the tree is determined
 * by the implementing [Spec] style.
 */
interface Scope {

  /**
   * Returns the 'name' of this scope. Which is the name of the test,
   * or the name of the scope that contains tests. So in the following example,
   *
   * <pre>
   * {@code
   * class StringSpecExample : AbstractStringSpec() {
   * init {
   *  "this is a test" {
   *       // test goes here
   *      }
   *    }
   *  }
   *  </pre>
   *
   *  The name of that test is "this is a test".
   */
  fun name(): String

  /**
   * Return's the [Description] instance for this [Scope] which
   * contains the name of this scope along with the parent names.
   */
  fun description(): Description
}