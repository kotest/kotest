package io.kotest.core.extensions

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * An [Extension] point that is invoked when a spec is selected and launched.
 */
interface SpecLaunchExtension : Extension {

   /**
    * Intercepts a [Spec] before it has been created.
    *
    * Implementations must invoke the [process] callback if they
    * wish this spec to be executed. If they want to skip this spec they
    * can choose to skip the callback.
    *
    * Any changes to the coroutine context are propagated downstream.
    *
    * @param spec the spec class that has been scheduled.
    * @param process callback function required to continue spec processing.
    */
   suspend fun launched(spec: KClass<*>, process: suspend (KClass<*>) -> Unit)
}
