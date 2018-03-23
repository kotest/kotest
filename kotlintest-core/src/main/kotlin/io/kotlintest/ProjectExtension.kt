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

  /**
   * Executed before each and every spec.
   * You must invoke next() to continue with the evaluation of the spec.
   * If you do not invoke next() then the spec is skipped.
   */
  fun interceptSpec(spec: Spec, next: () -> Unit) {}
}