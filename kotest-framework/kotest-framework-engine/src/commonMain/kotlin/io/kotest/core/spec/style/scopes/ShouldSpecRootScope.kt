package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.TestXMethod
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
      context(name = name, xmethod = TestXMethod.NONE, test = test)
   }

   /**
    * Adds a top level context scope to the spec.
    */
   fun fcontext(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
      context(name = name, xmethod = TestXMethod.FOCUSED, test = test)
   }

   /**
    * Adds a top level context scope to the spec.
    */
   fun xcontext(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
      context(name = name, xmethod = TestXMethod.DISABLED, test = test)
   }

   private fun context(
      name: String,
      xmethod: TestXMethod,
      test: suspend ShouldSpecContainerScope.() -> Unit
   ) {
      addContainer(
         testName = contextName(name),
         xmethod = xmethod,
         config = null
      ) { ShouldSpecContainerScope(this).test() }
   }

   /**
    * Adds a top level context scope accepting config to the spec.
    */
   fun context(name: String): RootContainerWithConfigBuilder<ShouldSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = contextName(name),
         xmethod = TestXMethod.NONE,
         context = this
      ) { ShouldSpecContainerScope(it) }

   fun fcontext(name: String): RootContainerWithConfigBuilder<ShouldSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = contextName(name),
         xmethod = TestXMethod.FOCUSED,
         context = this
      ) { ShouldSpecContainerScope(it) }

   /**
    * Adds a disabled top level context scope accepting config to the spec.
    */
   fun xcontext(name: String): RootContainerWithConfigBuilder<ShouldSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = contextName(name),
         xmethod = TestXMethod.DISABLED,
         context = this
      ) { ShouldSpecContainerScope(it) }

   /**
    * Adds a top level test, with the given name and test function, with test config supplied
    * by invoking [.config()][RootContainerWithConfigBuilder.config] on the return of this function.
    */
   fun should(name: String): RootContainerWithConfigBuilder<ShouldSpecContainerScope> =
      RootContainerWithConfigBuilder(
         context = this,
         name = shouldName(name),
         xmethod = TestXMethod.NONE
      ) { ShouldSpecContainerScope(it) }

   fun fshould(name: String): RootContainerWithConfigBuilder<ShouldSpecContainerScope> =
      RootContainerWithConfigBuilder(
         context = this,
         name = shouldName(name),
         xmethod = TestXMethod.FOCUSED
      ) { ShouldSpecContainerScope(it) }

   fun xshould(name: String): RootContainerWithConfigBuilder<ShouldSpecContainerScope> =
      RootContainerWithConfigBuilder(
         context = this,
         name = shouldName(name),
         xmethod = TestXMethod.DISABLED
      ) { ShouldSpecContainerScope(it) }

   /**
    * Adds a top level test, with the given name and test function, with default test config.
    */
   fun should(name: String, test: suspend TestScope.() -> Unit) {
      should(name = name, focused = false, xmethod = TestXMethod.NONE, test = test)
   }

   fun fshould(name: String, test: suspend TestScope.() -> Unit) {
      should(name = name, focused = true, xmethod = TestXMethod.FOCUSED, test = test)
   }

   fun xshould(name: String, test: suspend TestScope.() -> Unit) {
      should(name = name, focused = false, xmethod = TestXMethod.DISABLED, test = test)
   }

   private fun contextName(name: String) =
      TestNameBuilder.builder(name).withPrefix("context ").withDefaultAffixes().build()

   private fun shouldName(name: String) =
      TestNameBuilder.builder(name).withPrefix("should ").withDefaultAffixes().build()

   private fun should(name: String, focused: Boolean, xmethod: TestXMethod, test: suspend TestScope.() -> Unit) {
      addTest(
         testName = shouldName(name),
         xmethod = xmethod,
         config = null,
         test = test
      )
   }
}
