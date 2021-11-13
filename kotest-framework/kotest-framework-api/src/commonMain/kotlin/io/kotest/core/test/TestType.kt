package io.kotest.core.test

enum class TestType {

   /**
    * A container that can contain other tests.
    */
   Container,

   /**
    * A leaf test that cannot contain nested tests.
    */
   Test,
}
