package io.kotlintest

import io.kotlintest.extensions.SpecLevelExtension
import io.kotlintest.extensions.TestListener
import java.util.*
import kotlin.reflect.KProperty

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
  @Deprecated("Instead of this function, override isolationMode() which should return a IsolationMode value indicating how the isolation level should be set for this spec")
  fun isInstancePerTest(): Boolean

  fun isolationMode(): IsolationMode? = null

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
   *  These are the top level [TestCase] instances for this Spec.
   */
  fun testCases(): List<TestCase>

  fun hasFocusedTest(): Boolean = focused().isNotEmpty()

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

  fun description(): Description = Description.spec(this::class)
}

/**
 * A name slash id for this spec which is used as the parent route for tests.
 * By default this will return the fully qualified class name, unless the spec
 * class is annotated with @DisplayNamen.
 *
 * Note: This name must be globally unique. Two specs, even in different packages,
 * cannot share the same name.
 */
fun Class<out Spec>.displayName(): String {
  return when (val displayName = annotations.find { it is DisplayName }) {
    is DisplayName -> displayName.name
    else -> canonicalName
  }
}

fun Class<out Spec>.description() = Description.spec(this.displayName())

val Spec.listenerInstances by LazyWithReceiver<Spec, List<TestListener>> { this.listeners() }

private class LazyWithReceiver<This, Return>(val initializer: This.() -> Return) {
  private val values = WeakHashMap<This, Return>()
  
  @Suppress("UNCHECKED_CAST")
  operator fun getValue(thisRef: Any, property: KProperty<*>): Return = synchronized(values)
  {
    thisRef as This
    return values.getOrPut(thisRef) { thisRef.initializer() }
  }
}

/**
 * Returns the focused tests for this Spec. Can be empty if no test is marked as focused.
 */
fun Spec.focused(): List<TestCase> = testCases().filter { it.name.startsWith("f:") }
