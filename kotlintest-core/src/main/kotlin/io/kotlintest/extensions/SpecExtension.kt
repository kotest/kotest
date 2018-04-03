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
  @Deprecated("This interceptor function deprecated, use TestListener for the most common use cases or intercept(Description, Spec, Continuation) in this interface for more complicated requirements")
  fun intercept(spec: Spec, process: () -> Unit)

  /**
   * An extension function invoked to create an instance of a [Spec].
   *
   * An implementation can choose to create a new instance, or it can
   * choose to return null if it wishes to pass control to the next
   * extension (or if no more extensions, then back to the Test Runner).
   *
   * By overriding this function, extensions are able to customize
   * the way classes are created, to support things like constructors
   * with parameters, or classes that require special initization logic.
   */
  fun <T : Spec> instantiate(clazz: Class<T>): Spec? = null

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