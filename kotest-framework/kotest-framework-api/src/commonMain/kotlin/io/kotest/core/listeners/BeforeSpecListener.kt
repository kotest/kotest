package io.kotest.core.listeners

import io.kotest.core.spec.Spec

interface BeforeSpecListener : Listener {

   /**
    * This callback is invoked after the Engine instantiates a [Spec]
    * to be used as part of a [TestCase] execution.
    *
    * If a spec is instantiated multiple times - for example, if
    * [InstancePerTest] or [InstancePerLeaf] isolation
    * modes are used, then this callback will be invoked for each instance
    * created, just before the first test (or only test) is executed for that spec.
    *
    * This callback should be used if you need to perform setup
    * each time a new spec instance is created. If you simply need to
    * perform setup once per class file, then use [prepareSpec].
    *
    * @param spec the [Spec] instance.
    */
   suspend fun beforeSpec(spec: Spec): Unit = Unit
}
