package io.kotest.core.spec.style.scopes

import io.kotest.core.test.TestContext

interface FunSpecScope : RootScope {

   /**
    * Adds a top level [FunSpecContextScope] to the spec.
    */
   fun context(name: String, test: suspend FunSpecContextScope.() -> Unit) {
      registration().addContainerTest(name, xdisabled = false) {
         FunSpecContextScope(description().append(name), lifecycle(), this, defaultConfig()).test()
      }
   }

   /**
    * Adds a disabled top level [FunSpecContextScope] to the spec.
    */
   fun xcontext(name: String, test: suspend FunSpecContextScope.() -> Unit) {
      registration().addContainerTest(name, xdisabled = true) {}
   }

   fun test(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(name, registration(), xdisabled = false)

   fun xtest(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(name, registration(), xdisabled = true)

   /**
    * Adds a top level test, with the given name and test function, using the
    * resolved default test config.
    */
   fun test(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(name, xdisabled = false, test = test)

   fun xtest(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(name, xdisabled = true, test = test)
}
