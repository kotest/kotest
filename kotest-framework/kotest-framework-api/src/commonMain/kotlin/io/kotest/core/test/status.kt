package io.kotest.core.test

enum class TestStatus {
   // the test was skipped completely
   Ignored,

   // the test was successful
   Success,

   // the test failed because of some exception that was not an assertion error
   Error,

   // the test ran but an assertion failed
   Failure
}
