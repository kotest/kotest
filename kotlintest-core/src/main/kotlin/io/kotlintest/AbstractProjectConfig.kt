package io.kotlintest

import io.kotlintest.extensions.ProjectExtension
import io.kotlintest.extensions.ProjectLevelExtension
import io.kotlintest.extensions.TestListener

/**
 * Project-wide configuration. Extensions returned by an
 * instance of this class will be applied to all [Spec] and [TestScope]s.
 *
 * Create an object that is derived from this class, name the object `ProjectConfig`
 * and place it in your classpath in a package called `io.kotlintest.provided`.
 *
 * KotlinTest will detect its presence and use it when executing tests.
 *
 * Note: This is a breaking change from versions 2.0 and before, in which KotlinTest would
 * scan the classpath for instances of this class. It no longer does that, in favour
 * of the predefined package name + classname.
 */
abstract class AbstractProjectConfig {

  /**
   * List of project wide extensions, ie instances of [ProjectLevelExtension]
   */
  open fun extensions(): List<ProjectLevelExtension> = emptyList()

  /**
   * List of project wide [TestListener]s.
   */
  open fun listeners(): List<TestListener> = emptyList()

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

