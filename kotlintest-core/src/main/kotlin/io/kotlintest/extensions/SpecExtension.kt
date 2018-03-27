package io.kotlintest.extensions

import io.kotlintest.Spec

/**
 * Reusable spec extension to be registered with
 * [io.kotlintest.AbstractProjectConfig.specExtensions].
 */
interface SpecExtension {

  /**
   * You must invoke process() to continue with the evaluation of the spec.
   * If you do not invoke process() then the spec is skipped.
   *
   * @param spec the [Spec] instance that contains the tests
   * that will be executed if process is invoked.
   */
  fun intercept(spec: Spec, process: () -> Unit)
}