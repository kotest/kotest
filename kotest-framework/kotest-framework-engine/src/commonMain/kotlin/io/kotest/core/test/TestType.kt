package io.kotest.core.test

/**
 * Tests in Kotest are arranged in a tree with the "Engine" sitting at the root, and each
 * [Spec][io.kotest.core.spec.Spec] sitting directly under the Engine.
 *
 * Tests then sit under each [Spec][io.kotest.core.spec.Spec].
 *
 * * If a test is a leaf node and is not permitted to contain other tests, then we say it has type "Test".
 * * If a test is a parent node and is permitted to contain other nodes, then we say it is a "Container".
 */
enum class TestType {

   /**
    * A container that can contain other tests.
    * Used when a test has been defined programmatically via the DSL.
    */
   Container,

   /**
    * A leaf test that cannot contain nested tests.
    * Used when a test has been defined programmatically via the DSL.
    */
   Test,
}
