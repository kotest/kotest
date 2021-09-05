package io.kotest.core.listeners

import io.kotest.core.spec.Spec
import kotlin.reflect.KClass

/**
 * Invoked once per spec class if the spec has active tests.
 */
interface PrepareSpecListener : Listener {

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
   suspend fun prepareSpec(kclass: KClass<out Spec>): Unit = Unit
}
