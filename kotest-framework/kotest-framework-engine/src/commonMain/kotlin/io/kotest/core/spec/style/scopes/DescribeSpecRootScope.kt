package io.kotest.core.spec.style.scopes

import io.kotest.common.KotestInternal
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.TestConfig

/**
 * A context that allows root tests to be registered using the syntax:
 *
 * ```
 * describe("some test")
 * ```
 *
 * or
 *
 * ```
 * xdescribe("some disabled test")
 * ```
 */
interface DescribeSpecRootScope : RootScope {

   fun context(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      context(name, TestXMethod.NONE, test)
   }

   /**
    * Adds a test case with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   fun context(name: String, config: TestConfig, test: suspend DescribeSpecContainerScope.() -> Unit) {
      context(name, TestXMethod.NONE, test, config)
   }

   fun fcontext(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      context(name, TestXMethod.FOCUSED, test)
   }

   fun xcontext(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      context(name, TestXMethod.DISABLED, test)
   }

   fun context(name: String) = context(name, TestXMethod.NONE)
   fun fcontext(name: String) = context(name, TestXMethod.FOCUSED)
   fun xcontext(name: String) = context(name, TestXMethod.DISABLED)

   fun describe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      describe(name, TestXMethod.NONE, test)
   }

   /**
    * Adds a test case with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   fun describe(name: String, config: TestConfig, test: suspend DescribeSpecContainerScope.() -> Unit) {
      describe(name, TestXMethod.NONE, test, config)
   }

   fun fdescribe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      describe(name, TestXMethod.FOCUSED, test)
   }

   fun xdescribe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      describe(name, TestXMethod.DISABLED, test)
   }

   fun describe(name: String) = describe(name, TestXMethod.NONE)
   fun fdescribe(name: String) = describe(name, TestXMethod.FOCUSED)
   fun xdescribe(name: String) = describe(name, TestXMethod.DISABLED)

   fun it(name: String, test: suspend TestScope.() -> Unit) {
      it(name = name, xmethod = TestXMethod.NONE, test)
   }

   /**
    * Adds a test with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   fun it(name: String, config: TestConfig, test: suspend TestScope.() -> Unit) {
      it(name = name, xmethod = TestXMethod.NONE, test, config)
   }

   fun fit(name: String, test: suspend TestScope.() -> Unit) {
      it(name = name, xmethod = TestXMethod.FOCUSED, test)
   }

   fun xit(name: String, test: suspend TestScope.() -> Unit) {
      it(name = name, xmethod = TestXMethod.DISABLED, test)
   }

   private fun it(
      name: String,
      xmethod: TestXMethod,
      test: suspend TestScope.() -> Unit,
      config: TestConfig? = null
   ) {
      addTest(
         testName = TestNameBuilder.builder(name).build(),
         xmethod = xmethod,
         config = config,
         test = test
      )
   }

   private fun context(
      name: String,
      xmethod: TestXMethod,
      test: suspend DescribeSpecContainerScope.() -> Unit,
      config: TestConfig? = null
   ) {
      addContainer(
         TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         xmethod = xmethod,
         config = config,
      ) { DescribeSpecContainerScope(this).test() }
   }

   private fun describe(
      name: String,
      xmethod: TestXMethod
   ): RootContainerWithConfigBuilder<DescribeSpecContainerScope> {
      return RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         xmethod = xmethod,
         context = this,
      ) { DescribeSpecContainerScope(it) }
   }

   private fun context(
      name: String,
      xmethod: TestXMethod
   ): RootContainerWithConfigBuilder<DescribeSpecContainerScope> {
      return RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         xmethod = xmethod,
         context = this,
      ) { DescribeSpecContainerScope(it) }
   }

   private fun describe(
      name: String,
      xmethod: TestXMethod,
      test: suspend DescribeSpecContainerScope.() -> Unit,
      config: TestConfig? = null
   ) {
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         xmethod = xmethod,
         config = config
      ) { DescribeSpecContainerScope(this).test() }
   }
}
