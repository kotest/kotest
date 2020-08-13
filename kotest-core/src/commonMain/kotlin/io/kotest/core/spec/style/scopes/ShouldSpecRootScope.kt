package io.kotest.core.spec.style.scopes

import io.kotest.core.test.DescriptionType
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestName

/**
 * Allows tests to be registered in the 'ShouldSpec' fashion.
 *
 *  context("with context") {
 *    should("do something") {
 *      // test here
 *    }
 *  }
 *
 *  or
 *
 *  should("do something") {
 *    // test here
 *  }
 */
interface ShouldSpecRootScope : RootScope {

   /**
    * Adds a top level context scope to the spec.
    */
   fun context(name: String, test: suspend ShouldSpecContextScope.() -> Unit) {
      registration().addContainerTest(TestName(name), xdisabled = false) {
         ShouldSpecContextScope(
            description().append(TestName(name), DescriptionType.Container),
            lifecycle(),
            this,
            defaultConfig(),
            this.coroutineContext,
         ).test()
      }
   }

   /**
    * Adds a top level context scope to the spec.
    */
   fun xcontext(name: String, test: suspend ShouldSpecContextScope.() -> Unit) {
      registration().addContainerTest(TestName(name), xdisabled = true) {}
   }

   /**
    * Adds a top level test, with the given name and test function, with test config supplied
    * by invoking .config on the return of this function.
    */
   fun should(name: String) = RootTestWithConfigBuilder(TestName("should ", name), registration(), xdisabled = false)
   fun xshould(name: String) = RootTestWithConfigBuilder(TestName("should ", name), registration(), xdisabled = true)

   /**
    * Adds a top level test, with the given name and test function, with default test config.
    */
   fun should(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(TestName("should ", name), xdisabled = false, test = test)

   fun xshould(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(TestName("should ", name), xdisabled = true, test = test)
}
