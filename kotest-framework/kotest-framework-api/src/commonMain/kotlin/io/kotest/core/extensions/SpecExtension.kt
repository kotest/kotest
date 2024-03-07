@file:Suppress("DeprecatedCallableAddReplaceWith")

package io.kotest.core.extensions

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import kotlin.reflect.KClass

/**
 * An [Extension] point that allows intercepting execution of [Spec]s for
 * each spec instance that is created.
 */
interface SpecExtension : Extension {

   /**
    * Intercepts a [Spec] before it has been instantiated.
    *
    * Implementations must invoke the process callback if they
    * wish this spec to be executed. If they want to skip
    * the tests in this spec they can elect not to invoke
    * the callback.
    *
    * Once the [process] function returns, the execution of this
    * [Spec] and all it's nested [TestCase]s are guaranteed
    * to have been completed.
    *
    * @param process callback function required to continue spec processing
    */
   @Deprecated("This function had ambiguous specifications. Instead prefer intercept(spec: Spec, execute: suspend (Spec) -> Unit) which is guaranteed to run once per spec instance or use SpecRefExtension which runs once per spec class. This function was deprecated in 5.0")
   suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
      process()
   }

   /**
    * Intercepts a [Spec] before any tests are executed.
    *
    * Implementations must invoke the process callback if they
    * wish this spec to be executed. If they want to skip
    * the tests in this spec they can elect not to invoke
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
