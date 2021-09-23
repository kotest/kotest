package io.kotest.core.extensions

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase

/**
 * An [Extension] point that allows intercepting execution of [Spec]s for each
 * spec that is created.
 */
interface SpecInterceptExtension : Extension {

   /**
    * Intercepts a [Spec] after it has been instantiated but before
    * any tests are executed.
    *
    * Implementations must invoke the [process] callback if they
    * wish this spec to be executed. If they want to skip
    * the tests in this spec they can elect not to invoke
    * the callback.
    *
    * Once the [process] function returns, the execution of this
    * [Spec] and all it's nested [TestCase]s are guaranteed
    * to have been completed.
    *
    * This method is not invoked if the spec is inactive. An inactive spec
    * is one which has no active (enabled) tests.
    *
    * If you are using an isolation mode that calls for fresh instances of a
    * spec per test, then this method will be invoked for each instance created.
    *
    * @param spec the spec instance.
    * @param process callback function required to continue spec processing.
    */
   suspend fun interceptSpec(spec: Spec, process: suspend (Spec) -> Unit)
}
