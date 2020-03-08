package io.kotest.core.extensions

import io.kotest.core.test.TestCase
import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Reusable spec extension that allows intercepting specs before they are executed.
 * The callback is invoked for each [Spec] that has been submitted for execution.
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
    * [Spec] and all it's nested [TestCase]s are guaranteed
    * to have been completed.
    *
    * @param process callback function required to continue spec processing
    */
   suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit)
}
