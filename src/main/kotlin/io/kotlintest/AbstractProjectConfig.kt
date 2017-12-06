package io.kotlintest

/**
 * Project-wide configuration.
 *
 * Create an object that is derived from this class, call it io.kotlintest.provided.ProjectConfig,
 * and place it in your classpath. Then kotlintest will detect it's presence and use it when
 * executing tests.
 */
abstract class AbstractProjectConfig {

  /**
   * List of project-wide extensions. The [ProjectExtension.beforeAll] methods of the
   * [ProjectExtension]s are executed in the order of [ProjectExtension]s from left to right. The
   * [ProjectExtension.afterAll] methods are executed in reversed order (from right to left).
   */
  open val extensions: List<ProjectExtension> = listOf()

  /**
   * Executed before the first test of the project, but after the [ProjectExtension.beforeAll]
   * methods of extensions.
   */
  open fun beforeAll() {}

  /**
   * Executed after the last test of the project, but before the [ProjectExtension.afterAll]
   * methods.
   */
  open fun afterAll() {}
}