package io.kotlintest

import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.TestCaseExtension
import io.kotlintest.extensions.TestListener

/**
 * A [Spec] is the top level component in KotlinTest.
 *
 * It contains a single root [TestContainer] which in turn
 * contains [TestCase] instances or further scopes.
 *
 * A test case is the actual test unit. A test case will
 * never reside in a spec directly, but always as
 * part of a test scope.
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
 *
 * A [Spec] is also a [TestListener] to allow for
 * convenience overloads here if you just want to listen
 * in a single place.
 */
interface Spec : TestListener {

  /**
   * Returns true if this spec should use a new instance for
   * each test case. This is the default behavior in jUnit.
   *
   * If however you want a single instance to be shared for
   * all tests in the same class, like ScalaTest, then
   * this method should return false.
   *
   * Note: Not all spec types support allowing one instance
   * per test. This is due to implementation trickery required
   * with nested closures and junit test discovery.
   */
  fun isInstancePerTest(): Boolean

  /**
   * Intercepts the invocation of this spec instance.
   *
   * Override this function if you wish to add before and after
   * logic to the spec.
   *
   * If you wish to re-use intercept logic across multiple specs,
   * then look at [SpecExtension.intercept].
   *
   * This intercept function will be called once, before any of the
   * test cases in the spec are executed. Don't forget to call
   * `process()` in the body of this method otherwise the
   * execution of the spec will not continue.
   */
  fun interceptSpec(spec: Spec, process: () -> Unit) = process()

  /**
   * Intercepts the invocation of each test case.

   * Override this function if you wish to add before and after
   * logic to test case execution.
   *
   * If you wish to re-use intercept logic across multiple test cases,
   * then look at [TestCaseExtension.intercept].
   *
   * This intercept function will be called before each test case.
   * Don't forget to call `process()` in the body of this method.
   * Otherwise the execution of the test case will not continue.
   */
  fun interceptTestCase(testCase: TestCase, test: () -> Unit) = test()

  /**
   * Override this function to register instances of
   * [SpecExtension] which will intercept this spec.
   *
   * If you wish to register an extension for all specs
   * then use [AbstractProjectConfig.extensions].
   */
  fun specExtensions(): List<SpecExtension> = listOf()

  /**
   * Override this function to register instances of
   * [TestCaseExtension] which will intercept all
   * test cases in this spec.
   *
   * If you wish to register an extension for all test cases
   * then use [AbstractProjectConfig.extensions].
   */
  fun testCaseExtensions(): List<TestCaseExtension> = listOf()

  /**
   * Override this function to register instances of
   * [TestListener] which will be notified of events during
   * execution of this spec only.
   *
   * If you wish to register a listener that will be notified
   * for all specs, then use [AbstractProjectConfig.listeners].
   */
  fun listeners(): List<TestListener> = emptyList()

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

  /**
   * Returns the top level [TestScope] for this Spec.
   */
  fun root(): SpecScope
}