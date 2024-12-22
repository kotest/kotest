package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.test.TestScope

/**
 * Allows tests to be registered in the 'ShouldSpec' fashion.
 *
 * ```
 * context("with context") {
 *   should("do something") {
 *     // test here
 *   }
 * }
 * ```
 *
 *  or
 *
 * ```
 * should("do something") {
 *   // test here
 * }
 * ```
 */
interface ShouldSpecRootScope : RootScope {

   /**
    * Adds a top level context scope to the spec.
    */
   fun context(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
      context(name = name, disabled = false, test = test)
   }

   /**
    * Adds a top level context scope to the spec.
    */
   fun xcontext(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
      context(name = name, disabled = true, test = test)
   }

   private fun context(name: String, disabled: Boolean, test: suspend ShouldSpecContainerScope.() -> Unit) {
      addContainer(
         testName = TestName("context ", name, false),
         disabled = disabled,
         config = null
      ) { ShouldSpecContainerScope(this).test() }
   }

   /**
    * Adds a top level context scope accepting config to the spec.
    */
   fun context(name: String): RootContainerWithConfigBuilder<ShouldSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("context ", name, false), false, this) { ShouldSpecContainerScope(it) }

   /**
    * Adds a disabled top level context scope accepting config to the spec.
    */
   fun xcontext(name: String): RootContainerWithConfigBuilder<ShouldSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("context ", name, false), true, this) { ShouldSpecContainerScope(it) }

   /**
    * Adds a top level test, with the given name and test function, with test config supplied
    * by invoking [.config()][RootContainerWithConfigBuilder.config] on the return of this function.
    */
   fun should(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(context = this, name = TestName("should ", name, true), xdisabled = false)

   fun xshould(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(context = this, name = TestName("should ", name, true), xdisabled = true)

   /**
    * Adds a top level test, with the given name and test function, with default test config.
    */
   fun should(name: String, test: suspend TestScope.() -> Unit) {
      should(name, false, test)
   }

   fun xshould(name: String, test: suspend TestScope.() -> Unit) {
      should(name, true, test)
   }

   private fun should(name: String, xdisabled: Boolean, test: suspend TestScope.() -> Unit) {
      addTest(testName = TestName("should ", name, false), disabled = xdisabled, config = null, test = test)
   }
}
