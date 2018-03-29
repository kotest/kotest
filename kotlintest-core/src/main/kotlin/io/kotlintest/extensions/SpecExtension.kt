package io.kotlintest.extensions

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.Spec

/**
 * Reusable spec extension to be registered with
 * [AbstractProjectConfig.extensions].
 *
 * You must invoke process() otherwise the spec will
 * not be executed.
 */
interface SpecExtension : Extension {
  @Deprecated("This interceptor function deprecated, please consider using TestListener")
  fun intercept(spec: Spec, process: () -> Unit)
}