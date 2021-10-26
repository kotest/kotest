package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.test.TestContext

@Deprecated("Renamed to ShouldSpecRootContext. Deprecated since 4.5.")
typealias ShouldSpecRootScope = ShouldSpecRootContext

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
interface ShouldSpecRootContext : RootContext {

   /**
    * Adds a top level context scope to the spec.
    */
   fun context(name: String, test: suspend ShouldSpecContainerContext.() -> Unit) {
      addContainer(TestName("context ", name, false), false, null) {
         ShouldSpecContainerContext(this).test()
      }
   }

   /**
    * Adds a top level context scope to the spec.
    */
   fun xcontext(name: String, test: suspend ShouldSpecContainerContext.() -> Unit) {
      addContainer(TestName("context ", name, false), true, null) { ShouldSpecContainerContext(this).test() }
   }

   /**
    * Adds a top level context scope accepting config to the spec.
    */
   @ExperimentalKotest
   fun context(name: String): RootContextConfigBuilder<ShouldSpecContainerContext> =
      RootContextConfigBuilder(TestName("context ", name, false), false, this) { ShouldSpecContainerContext(it) }

   /**
    * Adds a disabled top level context scope accepting config to the spec.
    */
   @ExperimentalKotest
   fun xcontext(name: String): RootContextConfigBuilder<ShouldSpecContainerContext> =
      RootContextConfigBuilder(TestName("context ", name, false), true, this) { ShouldSpecContainerContext(it) }

   /**
    * Adds a top level test, with the given name and test function, with test config supplied
    * by invoking .config on the return of this function.
    */
   fun should(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(this, TestName("should ", name, true), false)

   fun xshould(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(this, TestName("should ", name, true), true)

   /**
    * Adds a top level test, with the given name and test function, with default test config.
    */
   fun should(name: String, test: suspend TestContext.() -> Unit) {
      addTest(TestName("should ", name, false), false, null, test)
   }

   fun xshould(name: String, test: suspend TestContext.() -> Unit) {
      addTest(TestName("should ", name, false), true, null, test)
   }
}
