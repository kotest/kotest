package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.test.TestContext

@Deprecated("Renamed to FunSpecRootContext. This typealias will be removed in 4.8")
typealias FunSpecRootScope = FunSpecRootContext

interface FunSpecRootContext : RootContext {

   /**
    * Adds a top level [FunSpecContainerContext] to this root scope.
    */
   fun context(name: String, test: suspend FunSpecContainerContext.() -> Unit) {
      val testName = TestName(name)
      registration().addContainerTest(testName, xdisabled = false) {
         FunSpecContainerContext(this).test()
      }
   }

   @ExperimentalKotest
   fun context(name: String) =
      RootContextConfigBuilder(TestName(name), registration(), false) { FunSpecContainerContext(it) }

   /**
    * Adds a disabled top level [FunSpecContainerContext] this root scope.
    */
   fun xcontext(name: String, test: suspend FunSpecContainerContext.() -> Unit) {
      registration().addContainerTest(TestName(name), xdisabled = true) {}
   }

   @ExperimentalKotest
   fun xcontext(name: String) =
      RootContextConfigBuilder(TestName(name), registration(), true) { FunSpecContainerContext(it) }

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
