package io.kotlintest.extensions

import io.kotlintest.AbstractProjectConfig

/**
 * Reusable extension to be registered with [AbstractProjectConfig.extensions].
 */
@Deprecated("Use TestListener and register with ProjectConfig.listeners")
interface ProjectExtension : ProjectLevelExtension {

  /**
   * Executed before the first test of the project and before [AbstractProjectConfig.beforeAll].
   */
  @Deprecated("Use TestListener.beforeProject and register with ProjectConfig.listeners")
  fun beforeAll() {}

  /**
   * Executed after the last test of the project and after [AbstractProjectConfig.afterAll]
   */
  @Deprecated("Use TestListener.afterProject and register with ProjectConfig.listeners")
  fun afterAll() {}
}