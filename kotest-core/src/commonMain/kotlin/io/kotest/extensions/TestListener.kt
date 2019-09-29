package io.kotest.extensions

import io.kotest.Description
import io.kotest.IsolationMode.InstancePerLeaf
import io.kotest.IsolationMode.InstancePerTest
import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult

interface TestListener {

  /**
   * Is invoked once the Test Engine is started.
   */
  @Deprecated("Prefer using ProjectListeners over TestListener for project before/after", ReplaceWith("ProjectListener"))
  fun beforeProject(): Unit = Unit

  /**
   * Is invoked once the Test Engine has finished.
   */
  @Deprecated("Prefer using ProjectListeners over TestListener for project before/after", ReplaceWith("ProjectListener"))
  fun afterProject(): Unit = Unit

  /**
   * This callback will be invoked before a [TestCase] is executed.
   *
   * If a test case is inactive (disabled), then this method will not
   * be invoked for that particular test case.
   *
   * @param testCase the [TestCase] about to be executed.
   */
  fun beforeTest(testCase: TestCase): Unit = Unit

  /**
   * This callback is invoked after a [TestCase] has finished.
   *
   * If a test case was skipped (ignored / disabled / inactive) then
   * this callback will not be invoked for that particular test case.
   *
   * @param testCase the [TestCase] that has completed.
   */
  fun afterTest(testCase: TestCase, result: TestResult): Unit = Unit

  /**
   * This callback is invoked after the Engine instantiates a [Spec]
   * to be used as part of a [TestCase] execution.
   *
   * If a spec is instantiated multiple times - for example, if
   * [InstancePerTest] or [InstancePerLeaf] isolation
   * modes are used, then this callback will be invoked for each instance
   * created, just before the first test (or only test) is executed for that spec.
   *
   * This callback should be used if you need to perform setup
   * each time a new spec instance is created. If you simply need to
   * perform setup once per class file, then use [beforeSpecClass].
   *
   * @param spec the [Spec] instance.
   */
  fun beforeSpec(spec: Spec): Unit = Unit

  /**
   * Is invoked after the [TestCase]s that are part of a particular
   * [Spec] instance have completed.
   *
   * If a spec is instantiated multiple times - for example, if
   * [InstancePerTest] or [InstancePerLeaf] isolation modes are used,
   * then this callback will be invoked for each instantiated spec,
   * after the tests that are applicable to that spec instance have
   * returned.
   *
   * This callback should be used if you need to perform cleanup
   * after each individual spec instance. If you simply need to
   * perform cleanup once per class file, then use [afterSpecClass].
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
   * The top level tests declared in the spec are supplied as a list of
   * instances of [TopLevelTest] which includes a flag set to true if
   * the test is active or false if inactive.
   *
   * If there are no active tests in a spec, then this callback will
   * still be invoked.
   *
   * The order of the list of tests is the same as the
   * order of execution.
   *
   * @param spec the [Spec] instance
   * @param tests the list of top level tests
   */
  fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>): Unit = Unit

  /**
   * Called once per [Spec], after all tests have completed for that spec.
   *
   * Regardless of how many times the spec is instantiated,
   * for example, if [InstancePerTest] or [InstancePerLeaf] isolation
   * modes are used, this callback will only be invoked once,
   * with the first instance of the Spec.
   *
   * The results parameter contains every [TestCase], along with
   * the result of that test, including tests that were ignored (which
   * will have a TestResult that has TestStatus.Ignored)
   *
   * @param spec the [Spec] instance
   * @param results a map of each test case mapped to its result.
   */
  fun afterSpecClass(spec: Spec, results: Map<TestCase, TestResult>): Unit = Unit

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
   * Is invoked each time a [Spec] is started.
   *
   * Note: If the spec is running with one instance per test, then this
   * function will be invoked multiple times.
   *
   * @param description the [Description] for the root [TestCase] of the spec.
   * @param spec the actual [Spec] instance.
   */
  @Deprecated("use beforeSpec(Spec)", ReplaceWith("beforeSpec(Spec)"))
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
  @Deprecated("use afterSpec(Spec)", ReplaceWith("afterSpec(Spec)"))
  fun afterSpec(description: Description, spec: Spec): Unit = Unit

  /**
   * This function will be invoked each time a new [TestCase] is executed.
   *
   * @param description the [Description] for the [TestCase] instance.
   */
  @Deprecated("use beforeTest(TestCase) which provides the full test case instance", ReplaceWith("beforeTest(TestCase)"))
  fun beforeTest(description: Description): Unit = Unit

  /**
   * Is invoked when a [TestCase] has finished. This includes when a test case
   * is ignored (skipped), passes (is successful), or fails (errors).
   *
   * @param description the [Description] for the [TestCase] instance.
   * @param result the [TestResult] which contains the outcome of the test.
   */
  @Deprecated("use afterTest(TestCase) which provides the full test case instance", ReplaceWith("afterTest(TestCase)"))
  fun afterTest(description: Description, result: TestResult): Unit = Unit

  @Deprecated("use beforeSpecClass(Spec, List<TopLevelTest>)", ReplaceWith("beforeSpecClass(Spec)"))
  fun beforeSpecStarted(description: Description, spec: Spec): Unit = Unit

  @Deprecated("use afterSpecClass(Spec, Map<TestCase, TestResult>) which provides the full test case instance", ReplaceWith("afterSpecClass(Spec, Map<TestCase, TestResult>)"))
  fun afterSpecCompleted(description: Description, spec: Spec): Unit = Unit
}

data class TopLevelTest(val testCase: TestCase, val order: Int)

data class TopLevelTests(val tests: List<TopLevelTest>)
