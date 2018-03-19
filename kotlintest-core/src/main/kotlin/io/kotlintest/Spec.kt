package io.kotlintest

/**
 * A [Spec] is the top level component in KotlinTest.
 *
 * It contains a single root [TestContainer] which in turn
 * contains [TestCase] instances or further containers.
 *
 * A test case is the actual test unit. A test case will
 * never reside in a spec directly, but always in the root
 * container or a child container of the root.
 *
 * Typically, users do not interact with instances of
 * [TestContainer] or [TestCase] directly, instead each
 * concrete implementation of Spec offers a different way to
 * create these using an easy to read DSL.
 *
 * For example, the FunSpec allows us to create tests using
 * the "test(name)" function, such as:
 *
 * fun test("this is a test") {
 *   // test code here
 * }
 *
 * The spec ultimately forms a tree, with the spec's root
 * container at the root, and nested containers forming
 * branches and test cases as the leaves. The actual hierarchy
 * will depend on the spec being used.
 */
interface Spec {

  /**
   * Returns true if this spec should use a new instance for
   * each test case. This is the default behavior in junit.
   *
   * If however you want a single instance to be shared for
   * all tests in the same class, like ScalaTest, then
   * this method should return false.
   */
  fun isInstancePerTest(): Boolean

  /**
   * Intercepts the call of the spec class.
   *
   * Override this function if you wish to control the way each spec
   * is executed.
   *
   * This means this interceptor will be called once, before any of the
   * testcases in the spec are executed.
   *
   * To continue execution of this spec class, you must invoke the spec
   * function. If you don't want to continue with the execution of the spec,
   * then do not invoke the spec function.
   */
  fun interceptSpec(spec: () -> Unit) {
    spec()
  }

  /**
   * Intercepts the call of each test case.
   *
   * Override this function if you wish to control the way each test
   * case is executed.
   *
   * Don't forget to call `test()` in the body of this method.
   * Otherwise the test case will never be executed.
   */
  fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
    test()
  }

  /**
   * Returns the top level root container for tests of this spec.
   */
  fun root(): TestContainer
}