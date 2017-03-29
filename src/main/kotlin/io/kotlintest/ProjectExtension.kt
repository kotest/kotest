package io.kotlintest

/**
 * Reusable extension to be registered with [ProjectConfig.extensions].
 */
interface ProjectExtension {

  /**
   * Executed before the first test of the project and before [ProjectConfig.beforeAll].
   */
  fun beforeAll() {}

  /**
   * Executed after the last test of the project and after [ProjectConfig.afterAll]
   */
  fun afterAll() {}
}