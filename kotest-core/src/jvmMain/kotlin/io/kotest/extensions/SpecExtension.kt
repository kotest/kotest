package io.kotest.extensions

import io.kotest.core.AbstractProjectConfig
import io.kotest.SpecInterface
import io.kotest.core.TestCase
import io.kotest.core.specs.Spec

/**
 * Reusable spec extension to be registered project wide
 * using [AbstractProjectConfig.extensions] or on a
 * per-spec basis by overriding `extensions()` in a [SpecInterface] class.
 */
interface SpecExtension : ProjectLevelExtension, SpecLevelExtension {

  /**
   * Intercepts execution of a [SpecInterface].
   *
   * Implementations must invoke the process callback if they
   * wish this spec to be executed. If they want to skip
   * the tests in this spec they can elect not to invoke
   * the callback.
   *
   * Once the process function returns, the execution of this
   * [SpecInterface] and all it's nested [TestCase]s are guaranteed
   * to have been completed.
   *
   * @param process callback function required to continue spec processing
   */
  @Deprecated("to be adapted")
  suspend fun intercept(spec: SpecInterface, process: suspend () -> Unit)

  suspend fun intercept(spec: Spec, process: suspend () -> Unit) {}
}
