package io.kotlintest.extensions

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.TestScope

interface TestListener {

  /**
   * This function will be invoked each time a new [TestCase] is executed.
   *
   * @param description the [Description] for the [TestCase] instance.
   */
  fun beforeTest(description: Description): Unit = Unit

  /**
   * Is invoked when a [TestCase] has finished. This includes when a test case
   * is ignored (skipped), passes (is successful), or fails (errors).
   *
   * @param description the [Description] for the [TestCase] instance.
   * @param result the [TestResult] which contains the outcome of the test.
   */
  fun afterTest(description: Description, result: TestResult): Unit = Unit

  /**
   * Is invoked each time a [Spec] is started.
   *
   * Note: If the spec is running with one instance per test, then this
   * function will be invoked multiple times.
   *
   * @param description the [Description] for the root [TestScope] of the spec.
   * @param spec the actual [Spec] instance.
   */
  fun beforeSpec(description: Description, spec: Spec): Unit = Unit

  /**
   * Is invoked each time a [Spec] completes.
   *
   * Note: If the spec is running with one instance per test, then this
   * function will be invoked multiple times.
   *
   * @param description the [Description] for the root [TestScope] of the spec.
   * @param spec the actual [Spec] instance.
   */
  fun afterSpec(description: Description, spec: Spec): Unit = Unit

  /**
   * Is invoked as soon as the Test Engine is started.
   */
  fun beforeProject(): Unit = Unit

  /**
   * Is invoked as soon as the Test Engine has finished.
   */
  fun afterProject(): Unit = Unit

  /**
   * Is invoked after all the [Spec] classes have been discovered.
   *
   * @param descriptions the [Description] instance for each Spec class discovered.
   */
  fun afterDiscovery(descriptions: List<Description>): Unit = Unit
}