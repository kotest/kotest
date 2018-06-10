package io.kotlintest

import io.kotlintest.extensions.SpecLevelExtension
import io.kotlintest.extensions.TestListener

/**
 * A [Spec] is the top level component in KotlinTest.
 *
 * It contains the root [TestCase] instances which in turn
 * can contain nested [TestCase] instances.

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
   * Override this function to register extensions
   * which will be invoked during execution of this spec.
   *
   * If you wish to register an extension across the project
   * then use [AbstractProjectConfig.extensions].
   */
  fun extensions(): List<SpecLevelExtension> = listOf()

  /**
   * Override this function to register instances of
   * [TestListener] which will be notified of events during
   * execution of this spec.
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
  fun name(): String = javaClass.displayName()

  fun description() = javaClass.description()

  /**
   *  These are the top level [TestCase] instances for this Spec.
   */
  fun testCases(): List<TestCase>

  fun closeResources()

  /**
   * Sets the order of top level [TestCase]s in this spec.
   * If this function returns a null value, then the value set in
   * the [AbstractProjectConfig] will be used.
   */
  fun testCaseOrder(): TestCaseOrder? = null

  /**
   * Any tags added here will be in applied to all [TestCase]s defined
   * in this [Spec] in addition to any defined on the individual
   * tests themselves.
   */
  fun tags(): Set<Tag> = emptySet()
}

fun Class<out Spec>.displayName(): String {
  val displayName = annotations.find { it is DisplayName }
  return when (displayName) {
    is DisplayName -> displayName.name
    else -> simpleName
  }
}

fun Class<out Spec>.description() = Description.root(this.displayName())