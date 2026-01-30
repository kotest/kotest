package io.kotest.core.spec.style

enum class TestXMethod {
   NONE,

   /**
    * If a test is explicitly focused through a keyword in the DSL, eg `ftest` or `fcontext`.
    */
   FOCUSED,

   /**
    * If a test is explicitly disabled through a keyword in the DSL, eg `xtest` or `xcontext`.
    */
   DISABLED
}
