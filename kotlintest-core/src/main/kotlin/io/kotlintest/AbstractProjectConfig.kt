package io.kotlintest

import io.kotlintest.extensions.ProjectExtension
import io.kotlintest.extensions.SpecExtension
import io.kotlintest.extensions.TestCaseExtension

/**
 * Project-wide configuration. Extensions returned by an
 * instance of this class will be applied to all specs and test cases.
 *
 * Create an object that is derived from this class, name the object `ProjectConfig`
 * and place it in your classpath in a package called `io.kotlintest.provided`.
 *
 * Kotlintest will detect it's presence and use it when executing tests.
 *
 * Note: This is a breaking change from versions 2.0 and prior, when KotlinTest would
 * scan the classpath for instances of this class. It no longer does that, in favour
 * of the predefined package name + classname.
 */
abstract class AbstractProjectConfig {

  /**
   * List of project-wide extensions. The [ProjectExtension.beforeAll] methods of
   * the [ProjectExtension]s are executed in the order of [ProjectExtension]s from
   * first to last. The [ProjectExtension.afterAll] methods are executed in reversed
   * order (from last to first).
   */
  open fun extensions(): List<ProjectExtension> = listOf()

  open fun specExtensions(): List<SpecExtension> = listOf()

  open fun testCaseExtensions(): List<TestCaseExtension> = listOf()

  /**
   * Override this function and return a number greater than 1 if you wish to
   * enable parallel execution of tests. The number returned is the number of
   * concurrently executing specs.
   *
   * An alternative way to enable this is the system property kotlintest.parallelism
   * which will always (if defined) take priority over the value here.
   */
  open fun parallelism(): Int = 1

  /**
   * Executed before the first test of the project, but after the
   * [ProjectExtension.beforeAll] methods.
   */
  open fun beforeAll() {}

  /**
   * Executed after the last test of the project, but before the
   * [ProjectExtension.afterAll] methods.
   */
  open fun afterAll() {}
}