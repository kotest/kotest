package io.kotest.core.listeners

import io.kotest.core.spec.Spec

@Suppress("DEPRECATION") // Remove when removing Listener
interface BeforeSpecListener : Listener {

   /**
    * This callback is invoked just before the first test (or only test) is executed
    * for a Spec.
    *
    * This callback is only invoked if the spec has active tests. If all tests in a spec
    * are disabled, or the spec has no tests defined, then this listener will NOT be invoked.
    *
    * Any errors in this listener will be propagated to the engine and further execution, including
    * [AfterSpecListener]s will be skipped. If you wish have before/after control even in the
    * case of exceptions, then consider using the [io.kotest.engine.spec.interceptor.SpecInterceptor].
    *
    * If a spec is instantiated multiple times - for example, if
    * [io.kotest.core.spec.IsolationMode.InstancePerTest] or
    * [io.kotest.core.spec.IsolationMode.InstancePerLeaf] isolation modes are used,
    * then this callback will be invoked for each instance created,
    *
    * This callback should be used if you need to perform setup
    * each time a new spec instance is created. If you simply need to
    * perform setup once per class file, then use [PrepareSpecListener].
    *
    * @param spec the [Spec] instance.
    */
   suspend fun beforeSpec(spec: Spec): Unit = Unit
}
