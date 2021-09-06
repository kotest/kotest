package io.kotest.core.listeners

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

typealias PrepareSpecListener = StartSpecListener

/**
 * Invoked once per spec class if the spec has active tests.
 */
interface StartSpecListener : Listener {

   /**
    * Called once per [Spec], when the engine is preparing to
    * execute the tests for that spec.
    *
    * Regardless of how many times the spec is instantiated,
    * for example, if [InstancePerTest] or [InstancePerLeaf] isolation
    * modes are used, this callback will only be invoked once.
    *
    * @param kclass the [Spec] class
    */
   suspend fun specStarted(kclass: KClass<out Spec>): Unit = Unit

   @Deprecated(
      "Use specStarted. This was deprecated in 5.0 and will be removed in 6.0",
      ReplaceWith("specStarted(kclass)")
   )
   suspend fun prepareSpec(kclass: KClass<out Spec>): Unit = Unit
}
