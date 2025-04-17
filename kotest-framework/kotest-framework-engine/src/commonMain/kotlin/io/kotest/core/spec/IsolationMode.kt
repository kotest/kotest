package io.kotest.core.spec

enum class IsolationMode {

   /**
    * A single instance of the [Spec] class is instantiated,
    * and then used for all [io.kotest.core.test.TestCase]s.
    */
   SingleInstance,

   /**
    * A new instance of the [Spec] class is instantiated for every top level (root)
    * [io.kotest.core.test.TestCase] and each root is executed in its own associated instance.
    */
   InstancePerRoot,
}
