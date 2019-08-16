package io.kotlintest.extensions

import io.kotlintest.AbstractProjectConfig

/**
 * Reusable extension to be registered with [AbstractProjectConfig.listeners].
 */
interface ProjectListener {

  /**
   * Executed before the first test of the project and before [AbstractProjectConfig.beforeAll].
   */
  fun beforeProject()

  /**
   * Executed after the last test of the project and after [AbstractProjectConfig.afterAll]
   */
  fun afterProject()
}