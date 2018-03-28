package io.kotlintest.extensions

import io.kotlintest.Spec

/**
 * Reusable spec extension to be registered with
 * [io.kotlintest.AbstractProjectConfig.specExtensions].
 */
interface SpecExtension {
  fun intercept(spec: Spec, process: () -> Unit)
}