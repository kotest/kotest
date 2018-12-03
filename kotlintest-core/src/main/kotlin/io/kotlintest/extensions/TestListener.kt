package io.kotlintest.extensions

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestIsolationMode.InstancePerLeaf
import io.kotlintest.TestIsolationMode.InstancePerTest
import io.kotlintest.TestResult

interface TestListener {

  /**
   * Is invoked when the Test Engine instantiates a [Spec] to be used
   * as part of a [TestCase] execution.
   *
   * If a spec is instantiated multiple times - for example, if
   * [InstancePerTest] or [InstancePerLeaf] isolation modes are used,
   * then this callback will be invoked for each instantiated spec,
   * just before the first test (or only test) is executed for that spec.
   *
   * This callback should be used if you need to perform setup
   * each time a fresh spec instance is created. If you simply need to
   * perform setup once per spec class, then use [prepareSpec].
   *
   * @param spec the [Spec] instance.
   */
  fun beforeSpec(spec: Spec): Unit = Unit

  /**
   * Is invoked after all [TestCase]s that are part of a particular
   * [Spec] instance have returned.
   *
   * If a spec is instantiated multiple times - for example, if
   * [InstancePerTest] or [InstancePerLeaf] isolation modes are used,
   * then this callback will be invoked for each instantiated spec,
   * after the tests that are applicable to that spec instance have
   * returned.
   *
   * This callback should be used if you need to perform cleanup
   * after each individual spec instance. If you simply need to
   * perform cleanup once per spec class, then use [cleanupSpec].
   *
   * @param spec the [Spec] instance.
   */
  fun afterSpec(spec: Spec): Unit = Unit

  /**
   * Called once per [Spec], when the engine is preparing to
   * execute the tests for that spec.
   *
   * Regardless of how many times the spec is instantiated,
   * for example, if [InstancePerTest] or [InstancePerLeaf] isolation
   * modes are used, this callback will only be invoked once,
   * with the first instance of the Spec.
   *
   * The top level tests declared in the spec are supplied in two lists -
   * one the active test list and the other the inactive test list.
   *
   * If there are no active tests in a spec, then this callback will
   * still be invoked.
   *
   * The order of the list of active tests is the same as the
   * order of execution.
   *
   * @param spec the [Spec] instance
   * @param active the list of active tests
   * @param active the list of inactive tests
   */
  fun prepareSpec(spec: Spec, active: List<TestCase>, inactive: List<TestCase>): Unit = Unit

  /**
   * Called once per [Spec], after all tests have completed for that spec.
   *
   * Regardless of how many times the spec is instantiated,
   * for example, if [InstancePerTest] or [InstancePerLeaf] isolation
   * modes are used, this callback will only be invoked once,
   * with the first instance of the Spec.
   *
   * The results parameter contains every test case, along with
   * the result of that test, including tests that were ignored.
   *
   * @param spec the [Spec] instance
   * @param results a map of each test case mapped to its result.
   */
  fun cleanupSpec(spec: Spec, results: Map<TestCase, TestResult>): Unit = Unit

  /**
   * This callback will be invoked before a [TestCase] is executed.
   *
   * If a test case is inactive (disabled), then this method will not
   * be invoked for that particular test case.
   *
   * @param test the [TestCase] about to be executed.
   */
  fun beforeTest(test: TestCase): Unit = Unit

  /**
   * This callback is invoked when a test is being skipped because it is inactive.
   *
   * @param test the ignored [TestCase].
   */
  fun ignoredTest(test: TestCase): Unit = Unit

  /**
   * This callback is invoked after a [TestCase] has finished.
   *
   * If a test case is inactive (disabled), then this method will not
   * be invoked for that particular test case.
   *
   * @param test the [TestCase] that has completed.
   */
  fun afterTest(test: TestCase, result: TestResult): Unit = Unit

  /**
   * Is invoked as soon as the Test Engine is started, before any other callback.
   */
  fun beforeProject(): Unit = Unit

  /**
   * Is invoked as soon as the Test Engine has finished, after all other callbacks.
   */
  fun afterProject(): Unit = Unit

  /**
   * Is invoked after all the [Spec] classes have been discovered.
   * This callback will be called after any [DiscoveryExtension]s have been invoked.
   *
   * In other words the descriptions given here are after discovery extensions
   * have had the chance to filter.
   *
   * @param descriptions the [Description] instance for each Spec class discovered.
   */
  fun afterDiscovery(descriptions: List<Description>): Unit = Unit

  /**
   * This callback will be invoked each time a new [TestCase] is executed.
   *
   * @param description the [Description] for the [TestCase] instance.
   */
  @Deprecated("use beforeTest(test)", ReplaceWith("beforeTest(test)"))
  fun beforeTest(description: Description): Unit = Unit

  /**
   * Is invoked when a [TestCase] has finished. This includes when a test case
   * is ignored (skipped), passes (is successful), or fails (errors).
   *
   * @param description the [Description] for the [TestCase] instance.
   * @param result the [TestResult] which contains the outcome of the test.
   */
  @Deprecated("use afterTest(test, result)", ReplaceWith("afterTest(test, result)"))
  fun afterTest(description: Description, result: TestResult): Unit = Unit

  /**
   * Is invoked each time a [Spec] is started.
   *
   * Note: If the spec is running with one instance per test, then this
   * function will be invoked multiple times.
   *
   * @param description the [Description] for the root [TestCase] of the spec.
   * @param spec the actual [Spec] instance.
   */
  @Deprecated("use beforeSpec(spec)", ReplaceWith("beforeSpec(spec)"))
  fun beforeSpec(description: Description, spec: Spec): Unit = Unit


  /**
   * Is invoked each time a [Spec] completes.
   *
   * Note: If the spec is running with one instance per test, then this
   * function will be invoked multiple times.
   *
   * @param description the [Description] for the root [TestCase] of the spec.
   * @param spec the actual [Spec] instance.
   */
  @Deprecated("use afterSpec(spec)", ReplaceWith("afterSpec(spec)"))
  fun afterSpec(description: Description, spec: Spec): Unit = Unit
}