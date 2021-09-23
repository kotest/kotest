package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.test.TestContext

@Deprecated("Renamed to FunSpecRootContext. Deprecated since 4.5.")
typealias FunSpecRootScope = FunSpecRootContext

interface FunSpecRootContext : RootContext {

   /**
    * Adds a top level [FunSpecContainerContext] to this root scope.
    */
   fun context(name: String, test: suspend FunSpecContainerContext.() -> Unit) {
      val testName = TestName("context", name, false)
      registration().addContainerTest(testName, xdisabled = false) {
         val incomplete = IncompleteContainerContext(this)
         FunSpecContainerContext(incomplete).test()
         if (!incomplete.registered) throw IncompleteContainerException(testName.testName)
      }
   }

   @ExperimentalKotest
   fun context(name: String) =
      RootContextConfigBuilder(TestName("context", name, false), registration(), false) { FunSpecContainerContext(it) }

   /**
    * Adds a disabled top level [FunSpecContainerContext] this root scope.
    */
   fun xcontext(name: String, test: suspend FunSpecContainerContext.() -> Unit) {
      registration().addContainerTest(TestName("context", name, false), xdisabled = true) {}
   }

   @ExperimentalKotest
   fun xcontext(name: String) =
      RootContextConfigBuilder(TestName("context", name, false), registration(), true) { FunSpecContainerContext(it) }

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
