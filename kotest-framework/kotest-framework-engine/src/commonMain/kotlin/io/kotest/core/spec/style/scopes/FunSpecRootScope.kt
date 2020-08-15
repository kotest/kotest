package io.kotest.core.spec.style.scopes

import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestContext

interface FunSpecRootScope : RootScope {

   /**
    * Adds a top level [FunSpecContextScope] to this root scope.
    */
   fun context(name: String, test: suspend FunSpecContextScope.() -> Unit) {
      val dname = DescriptionName.TestName(name)
      registration().addContainerTest(dname, xdisabled = false) {
         FunSpecContextScope(
            description().append(dname, io.kotest.core.test.TestType.Container),
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
      registration().addContainerTest(DescriptionName.TestName(name), xdisabled = true) {}
   }

   /**
    * Adds a top level test case to this root scope.
    */
   fun test(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(DescriptionName.TestName(name), registration(), xdisabled = false)

   /**
    * Adds a top level test, with the given name and test function, using the
    * resolved default test config.
    */
   fun test(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(DescriptionName.TestName(name), xdisabled = false, test = test)

   /**
    * Adds a disabled top level test case to this root scope.
    */
   fun xtest(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(DescriptionName.TestName(name), xdisabled = true, test = test)

   /**
    * Adds a disabled top level test case with config to this root scope.
    */
   fun xtest(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(DescriptionName.TestName(name), registration(), xdisabled = true)
}
