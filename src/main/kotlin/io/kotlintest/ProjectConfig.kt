package io.kotlintest

/**
 * Project-wide configuration.
 *
 * Create an object that is derived from this class and place it in the (top-level) test package.
 */
abstract class ProjectConfig {

  /**
   * List of project-wide extensions, executed from left to right.
   */
  open val extensions: List<ProjectExtension> = listOf()

  /**
   * Executed before the first test of the project.
   */
  open fun beforeAll() {}

  /**
   * Executed after the last test of the project.
   */
  open fun afterAll() {}
}