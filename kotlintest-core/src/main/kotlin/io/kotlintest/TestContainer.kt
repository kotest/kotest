package io.kotlintest

/**
 * A [TestScope] is a block of code that acts as a node in the test plan tree.
 * A scope can either be a leaf level [TestCase] - which you can think of as a
 * unit test - and branch level [TestContainer]s. Containers can nest
 * further containers - the actual structure of the tree is determined
 * by the implementing [Spec] style.
 */
interface TestScope {

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

  /**
   * Returns a reference to the [Spec] instance that this scope is associated with.
   */
  fun spec(): Spec
}

class SpecScope(val description: Description,
                val spec: Spec,
                val scopes: List<TestScope>) : TestScope {
  override fun name(): String = description.name
  override fun description(): Description = description
  override fun spec(): Spec = spec
}

/**
 * Used to group together [TestCase] instances
 * for heirarchical display and execution order.
 *
 * All testcases must reside in a [TestContainer].
 *
 * A container has a name, which is used when outputting
 * the hierarchical location of tests.
 *
 * It also has a reference back to the parent spec
 * so that we can generate a link to the source file
 * for any given test.
 *
 * Fianlly it captures a closure of the body of the container.
 * This is a function which is invoked with a [TestContext],
 * which can, at runtime, register further [TestScope]s with the
 * test plan.
 *
 * This function is designed so that the closures which
 * are used by the spec DSLs can be executed a later
 * stage, rather than when the class is constructed.
 *
 * This allows side effects inside a container to be
 * deferred until the test engine is ready to execute
 * tests inside that particular container.
 */
class TestContainer(val description: Description,
                    val spec: Spec,
                    val closure: (TestContext) -> Unit) : TestScope {
  override fun name(): String = description.name
  override fun description(): Description = description
  override fun spec(): Spec = spec
}

fun lineNumber(): Int {
  val stack = Throwable().stackTrace
  return stack.dropWhile {
    it.className.startsWith("io.kotlintest")
  }[0].lineNumber
}