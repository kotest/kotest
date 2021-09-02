package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName

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
@KotestDsl
interface ShouldSpecRootContext : RootContext {

   /**
    * Adds a top level context scope to the spec.
    */
   fun context(name: String, test: suspend ShouldSpecContainerContext.() -> Unit) {
      registration().addContainerTest(createTestName(name), xdisabled = false) {
         ShouldSpecContainerContext(this).test()
      }
   }

   /**
    * Adds a top level context scope accepting config to the spec.
    */
   @ExperimentalKotest
   fun context(name: String) =
      RootContextConfigBuilder(createTestName(name), registration(), false) { ShouldSpecContainerContext(it) }

   /**
    * Adds a top level context scope to the spec.
    */
   fun xcontext(name: String, test: suspend ShouldSpecContainerContext.() -> Unit) {
      registration().addContainerTest(createTestName(name), xdisabled = true) {}
   }

   /**
    * Adds a disabled top level context scope accepting config to the spec.
    */
   @ExperimentalKotest
   fun xcontext(name: String) =
      RootContextConfigBuilder(createTestName(name), registration(), true) { ShouldSpecContainerContext(it) }

   /**
    * Adds a top level test, with the given name and test function, with test config supplied
    * by invoking .config on the return of this function.
    */
   fun should(name: String) =
      RootTestWithConfigBuilder(createTestName("should ", name, true), registration(), xdisabled = false)

   fun xshould(name: String) =
      RootTestWithConfigBuilder(createTestName("should ", name, true), registration(), xdisabled = true)

   /**
    * Adds a top level test, with the given name and test function, with default test config.
    */
   fun should(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(createTestName("should ", name, true), xdisabled = false, test = test)

   fun xshould(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(createTestName("should ", name, true), xdisabled = true, test = test)

}
