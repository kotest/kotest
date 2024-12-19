package io.kotest.core.spec

enum class IsolationMode {

   /**
    * A single instance of the [Spec] class is instantiated,
    * and then used for every [io.kotest.core.test.TestCase].
    *
    * Test cases will be executed as soon as they are discovered.
    */
   SingleInstance,

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
   InstancePerLeaf
}
