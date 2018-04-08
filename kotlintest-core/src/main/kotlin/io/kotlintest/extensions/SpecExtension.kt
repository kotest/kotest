package io.kotlintest.extensions

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestScope

/**
 * Reusable spec extension to be registered project wide
 * using [AbstractProjectConfig.extensions] or on a
 * per-spec basis by overriding `extensions()` in a [Spec] class.
 */
interface SpecExtension : Extension {

  /**
   * Intercepts execution of a [Spec].
   *
   * Implementations must invoke the process callback if they
   * wish this spec to be executed. If they want to skip
   * the tests in this spec they can elect not to invoke
   * the callback.
   *
   * Once the process function returns, the execution of this
   * [Spec] and all it's nested [TestScope]s are guaranteed
   * to have been completed.
   *
   * @param description the name and parents of the spec
   * @param spec the instance of the spec itself
   * @param process callback function required to continue spec processing
   */
  fun intercept(description: Description, spec: Spec, process: () -> Unit) = process()
}