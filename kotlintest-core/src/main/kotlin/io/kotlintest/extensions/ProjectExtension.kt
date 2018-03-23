package io.kotlintest.extensions

/**
 * Reusable extension to be registered with [io.kotlintest.AbstractProjectConfig.extensions].
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