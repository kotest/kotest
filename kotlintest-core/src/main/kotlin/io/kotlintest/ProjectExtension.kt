package io.kotlintest

/**
 * Reusable extension to be registered with [AbstractProjectConfig.extensions].
 */
interface ProjectExtension {

  /**
   * Executed before the first test of the project and before [AbstractProjectConfig.beforeAll].
   */
  fun beforeAll() {}

  /**
   * Executed after the last test of the project and after [AbstractProjectConfig.afterAll]
   */
  fun afterAll() {}
}