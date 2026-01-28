package io.kotest.core.spec.style.scopes

import io.kotest.common.KotestInternal
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.TestConfig

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
    * Adds a top-level context scope to the spec.
    */
   fun context(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
      context(name = name, xmethod = TestXMethod.NONE, test = test)
   }

   /**
    * Adds a test case with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   fun context(name: String, config: TestConfig, test: suspend ShouldSpecContainerScope.() -> Unit) {
      context(name = name, xmethod = TestXMethod.NONE, test = test, config = config)
   }

   /**
    * Adds a top-level context scope to the spec.
    */
   fun fcontext(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
      context(name = name, xmethod = TestXMethod.FOCUSED, test = test)
   }

   /**
    * Adds a top-level context scope to the spec.
    */
   fun xcontext(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
      context(name = name, xmethod = TestXMethod.DISABLED, test = test)
   }

   private fun context(
      name: String,
      xmethod: TestXMethod,
      test: suspend ShouldSpecContainerScope.() -> Unit,
      config: TestConfig? = null
   ) {
      addContainer(
         testName = contextName(name),
         xmethod = xmethod,
         config = config
      ) { ShouldSpecContainerScope(this).test() }
   }

   /**
    * Adds a top-level context scope accepting config to the spec.
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
    * Adds a disabled top-level context scope accepting config to the spec.
    */
   fun xcontext(name: String): RootContainerWithConfigBuilder<ShouldSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = contextName(name),
         xmethod = TestXMethod.DISABLED,
         context = this
      ) { ShouldSpecContainerScope(it) }

   /**
    * Adds a top level test, with the given name and test function, with test config supplied
    * by invoking `.config` on the return of this function.
    */
   fun should(name: String): RootTestWithConfigBuilder = should(name, TestXMethod.NONE)

   /**
    * Adds a focused top level test, with the given name and test function, with test config supplied
    * by invoking `.config` on the return of this function.
    */
   fun fshould(name: String): RootTestWithConfigBuilder = should(name, TestXMethod.FOCUSED)

   /**
    * Adds a disabled top level test, with the given name and test function, with test config supplied
    * by invoking `.config` on the return of this function.
    */
   fun xshould(name: String): RootTestWithConfigBuilder = should(name, TestXMethod.DISABLED)

   /**
    * Adds a top-level test, with the given name and test function, with default test config.
    */
   fun should(name: String, test: suspend TestScope.() -> Unit) {
      should(name = name, xmethod = TestXMethod.NONE, test = test)
   }

   /**
    * Adds a test with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   fun should(name: String, config: TestConfig, test: suspend TestScope.() -> Unit) {
      should(name = name, xmethod = TestXMethod.NONE, test = test, config = config)
   }

   /**
    * Adds a focused top-level test, with the given name and test function, with default test config.
    */
   fun fshould(name: String, test: suspend TestScope.() -> Unit) {
      should(name = name, xmethod = TestXMethod.FOCUSED, test = test)
   }

   /**
    * Adds a disabled top-level test, with the given name and test function, with default test config.
    */
   fun xshould(name: String, test: suspend TestScope.() -> Unit) {
      should(name = name, xmethod = TestXMethod.DISABLED, test = test)
   }

   private fun contextName(name: String) =
      TestNameBuilder.builder(name).withPrefix("context ").withDefaultAffixes().build()

   private fun shouldName(name: String) =
      TestNameBuilder.builder(name).withPrefix("should ").withDefaultAffixes().build()

   private fun should(name: String, xmethod: TestXMethod, test: suspend TestScope.() -> Unit, config: TestConfig? = null) {
      addTest(
         testName = shouldName(name),
         xmethod = xmethod,
         config = config,
         test = test,
      )
   }

   private fun should(name: String, xmethod: TestXMethod): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = shouldName(name),
         xmethod = xmethod,
      )
   }
}
