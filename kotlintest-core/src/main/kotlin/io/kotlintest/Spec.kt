package io.kotlintest

/**
 * A [Spec] is the top level component in KotlinTest.
 *
 * It contains a single root [TestScope] which in turn
 * contains [TestCase] instances or further scopes.
 *
 * A test case is the actual test unit. A test case will
 * never reside in a spec directly, but always as
 * part of a test scope.
 *
 * Typically, users do not interact with instances of
 * [TestScope] or [TestCase] directly, instead each
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
   * each test case. This is the default behavior in jUnit.
   *
   * If however you want a single instance to be shared for
   * all tests in the same class, like ScalaTest, then
   * this method should return false.
   */
  fun isInstancePerTest(): Boolean

  /**
   * Intercepts the invocation of the spec class.
   *
   * Override this function if you wish to control how each
   * spec is executed.
   *
   * The interceptor will be called once per spec, before any of the
   * testcases in the spec are executed.
   *
   * Don't forget to call `test()` in the body of this method.
   * Otherwise the test case will never be executed.
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
   * Returns the top level root [TestScope] for this Spec.
   */
  fun scope(): TestScope

  /**
   * A Readable name for this spec. By default will use the
   * simple class name, unless @DisplayName is used to annotate
   * the spec. Alternatively, a user can override this function
   * to return a customized name.
   */
  fun name(): String {
    val displayName = this::class.annotations.find { it is DisplayName }
    return when (displayName) {
      is DisplayName -> displayName.name
      else -> javaClass.simpleName
    }
  }
}