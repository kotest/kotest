package io.kotest.core.extensions

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase

/**
 * An [Extension] point that allows intercepting execution of [Spec]s for
 * each spec instance that is created.
 */
interface SpecExtension : Extension {

   /**
    * Intercepts a [Spec] before any tests are executed.
    *
    * Implementations must invoke the process callback if they
    * wish this spec to be executed. If they want to skip
    * the tests in this spec they can elect not to invokeRemove
    * the callback.
    *
    * Once the [execute] function returns, the execution of this
    * [Spec] and all it's nested [TestCase]s are guaranteed
    * to have been completed.
    *
    * Any changes to the coroutine context are propagated downstream.
    */
   suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      execute(spec)
   }
}
