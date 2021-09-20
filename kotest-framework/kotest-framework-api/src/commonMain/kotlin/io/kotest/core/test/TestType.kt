package io.kotest.core.test

enum class TestType {

   /**
    * A container that must contain at least one other test.
    */
   Container,

   /**
    * A leaf test that cannot contain nested tests.
    */
   Test,

   /**
    * A container of tests that may be empty.
    * This is used by dynamic code generation, eg data testing.
    */
   ContainerAndTest,
}
