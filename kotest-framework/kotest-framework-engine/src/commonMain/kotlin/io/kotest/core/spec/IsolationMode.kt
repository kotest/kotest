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

   /**
    * A new instance of the [Spec] class is instantiated for every
    * [io.kotest.core.test.TestCase] - both containers and leaf tests - and they are
    * executed once the previous test has completed.
    *
    * For example, in the following test plan:
    *
    * ```
    * "this test" {
    *   println("a")
    *   "nested test" {
    *     println("b")
    *   }
    *   "nested test 2" {
    *     println("c")
    *   }
    * }
    * ```
    *
    * The output will be:
    *
    * ```text
    * a
    * a
    * b
    * a
    * c
    * ```
    *
    */
   @Deprecated("The semantics of instance per leaf are inconsistent and this mode should be avoided. InstancePerRoot is recommended if you want to isolate your tests.")
   InstancePerTest,

   /**
    * A new instance of the [Spec] class is instantiated for every
    * leaf [io.kotest.core.test.TestCase]. Container test cases are executed only as
    * part of the path to a leaf test.
    *
    * For example, in the following test plan:
    *
    * ```
    * "this test" {
    *   println("a")
    *   "nested test" {
    *     println("b")
    *   }
    *   "nested test 2" {
    *     println("c")
    *   }
    * }
    * ```
    *
    * The output will be:
    *
    * ```text
    * a
    * b
    * a
    * c
    * ```
    */
   @Deprecated("The semantics of instance per leaf are inconsistent and this mode should be avoided. InstancePerRoot is recommended if you want to isolate your tests.")
   InstancePerLeaf
}
