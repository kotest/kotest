package io.kotest.core.test

enum class TestStatus {
   /** The test was skipped completely */
   Ignored,

   /** The test was successful */
   Success,

   /** The test failed because of some exception that was not an assertion error */
   Error,

   /** The test ran but an assertion failed */
   Failure
}
