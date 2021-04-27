package io.kotest.core.spec.style.scopes

import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName

@Deprecated("Renamed to FunSpecRootContext. This typealias will be removed in 4.8")
typealias FunSpecRootScope = FunSpecRootContext

interface FunSpecRootContext : RootContext {

   /**
    * Adds a top level [FunSpecContainerContext] to this root scope.
    */
   fun context(name: String, test: suspend FunSpecContainerContext.() -> Unit) {
      val testName = createTestName(name)
      registration().addContainerTest(testName, xdisabled = false) {
         FunSpecContainerContext(this).test()
      }
   }

   /**
    * Adds a disabled top level [FunSpecContainerContext] this root scope.
    */
   fun xcontext(name: String, test: suspend FunSpecContainerContext.() -> Unit) {
      registration().addContainerTest(createTestName(name), xdisabled = true) {}
   }

   /**
    * Adds a top level test case to this root scope.
    */
   fun test(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(createTestName(name), registration(), xdisabled = false)

   /**
    * Adds a top level test, with the given name and test function, using the
    * resolved default test config.
    */
   fun test(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(createTestName(name), xdisabled = false, test = test)

   /**
    * Adds a disabled top level test case to this root scope.
    */
   fun xtest(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(createTestName(name), xdisabled = true, test = test)

   /**
    * Adds a disabled top level test case with config to this root scope.
    */
   fun xtest(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(createTestName(name), registration(), xdisabled = true)
}
