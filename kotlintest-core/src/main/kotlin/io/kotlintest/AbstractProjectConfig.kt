package io.kotlintest

/**
 * Project-wide configuration. Instances of [ProjectExtension] returned by an
 * instance of this class will be applied to all testcases.
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
   * List of project-wide extensions. The [ProjectExtension.beforeAll] methods of the
   * [ProjectExtension]s are executed in the order of [ProjectExtension]s from left to right. The
   * [ProjectExtension.afterAll] methods are executed in reversed order (from right to left).
   */
  open val extensions: List<ProjectExtension> = listOf()

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

  /**
   * Executed before each and every [Spec].
   * You must invoke next() to continue with the evaluation of the spec.
   * If you do not invoke next() then the spec is skipped.
   */
  fun interceptSpec(spec: Spec, next: () -> Unit) {}
}