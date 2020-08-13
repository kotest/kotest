package io.kotest.core.spec.style.scopes

import io.kotest.core.test.TestContext
import io.kotest.core.test.TestName
import io.kotest.core.test.TestType

interface FunSpecRootScope : RootScope {

   /**
    * Adds a top level [FunSpecContextScope] to this root scope.
    */
   fun context(name: String, test: suspend FunSpecContextScope.() -> Unit) {
      registration().addContainerTest(TestName(name), xdisabled = false) {
         FunSpecContextScope(
            description().append(TestName(name), TestType.Container),
            lifecycle(),
            this,
            defaultConfig(),
            this.coroutineContext,
         ).test()
      }
   }

   /**
    * Adds a disabled top level [FunSpecContextScope] this root scope.
    */
   fun xcontext(name: String, test: suspend FunSpecContextScope.() -> Unit) {
      registration().addContainerTest(TestName(name), xdisabled = true) {}
   }

   /**
    * Adds a top level test case to this root scope.
    */
   fun test(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(TestName(name), registration(), xdisabled = false)

   /**
    * Adds a top level test, with the given name and test function, using the
    * resolved default test config.
    */
   fun test(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(TestName(name), xdisabled = false, test = test)

   /**
    * Adds a disabled top level test case to this root scope.
    */
   fun xtest(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(TestName(name), xdisabled = true, test = test)

   /**
    * Adds a disabled top level test case with config to this root scope.
    */
   fun xtest(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(TestName(name), registration(), xdisabled = true)
}
