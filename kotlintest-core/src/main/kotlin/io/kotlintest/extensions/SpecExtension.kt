package io.kotlintest.extensions

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.Spec

/**
 * Reusable spec extension to be registered project wide
 * using [AbstractProjectConfig.extensions] or on a
 * per-spec basis by overriding `extensions()` in a [Spec] class.
 */
interface SpecExtension : Extension {

  /**
   * You must invoke process() otherwise the spec will
   * not be executed.
   */
  @Deprecated("This interceptor function deprecated, use TestListener for the most common use cases or intercept(Description, Spec, Continuation) in this interface for more complicated requirements", ReplaceWith("intercept()"))
  fun intercept(spec: Spec, process: () -> Unit) = process()

//  /**
//   * Intercepts execution of a [Spec].
//   *
//   * Implementations must either invoke one of the functions
//   * of the [Continuation] parameter or throw an exception.
//   * Otherwise, the Test Runner will continue to wait for the Spec
//   * to be completed indefinitely.
//   *
//   * @param description the name and parents of the spec
//   * @param spec the instance of the spec itself
//   * @param continuation callback function required to notify
//   * the Test Runner of the status of the interception.
//   */
//  fun intercept(description: Description, spec: Spec, continuation: Continuation)
}

interface Continuation {
  fun run()
  fun skip()
  fun abort(t: Throwable)
}