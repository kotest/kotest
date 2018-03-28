package io.kotlintest.extensions

import io.kotlintest.Spec

/**
 * Reusable spec extension to be registered with
 * [io.kotlintest.AbstractProjectConfig.specExtensions].
 *
 * You must invoke process() otherwise the spec will
 * not be executed.
 */
interface SpecExtension {
  fun intercept(spec: Spec, process: () -> Unit)
}