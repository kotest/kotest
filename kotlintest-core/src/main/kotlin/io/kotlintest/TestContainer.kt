package io.kotlintest

/**
 * A [TestScope] is a block of code that acts as a node in the test plan tree.
 * A scope can either be a leaf level [TestCase] - which you can think of as a
 * unit test - and branch level [TestContainer]s. Containers can nest
 * further containers - the actual structure of the tree is determined
 * by the implementing [Spec] style.
 */
interface TestScope {

  fun name(): String

  /**
   * Returns a '/' delimited string which is the full path to the test case.
   * That is, the string contains the names of all parent scopes flattened.
   * This is useful when you want to use a [TestCaseExtension] for tests
   * which have different tree locations but the same final name.
   */
  fun path(): String

  /**
   * Returns a reference to the [Spec] instance that this scope is associated with.
   */
  fun spec(): Spec
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
class TestContainer(val displayName: String,
                    val path: String,
                    val spec: Spec,
                    val closure: (TestContext) -> Unit,
                    val isSpecRoot: Boolean = false) : TestScope {
  override fun name(): String = displayName
  override fun path(): String = path
  override fun spec(): Spec = spec
}

fun lineNumber(): Int {
  val stack = Throwable().stackTrace
  return stack.dropWhile {
    it.className.startsWith("io.kotlintest")
  }[0].lineNumber
}