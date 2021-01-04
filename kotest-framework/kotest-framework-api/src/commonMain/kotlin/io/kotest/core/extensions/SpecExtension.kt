package io.kotest.core.extensions

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import kotlin.reflect.KClass

/**
 * Extension point that allows intercepting execution of specs.
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
   suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
      process()
   }

   /**
    * Implementations must invoke the process callback if they
    * wish this spec to be executed. If they want to skip
    * the tests in this spec they can elect not to invoke
    * the callback.
    *
    * Once the [execute] function returns, the execution of this
    * [Spec] and all it's nested [TestCase]s are guaranteed
    * to have been completed.
    */
   suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {}
}
