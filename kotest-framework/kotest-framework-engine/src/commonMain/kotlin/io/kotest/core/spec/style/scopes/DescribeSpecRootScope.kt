package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.test.TestScope

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
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         focused = false,
         disabled = false,
         config = null,
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun fcontext(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         focused = true,
         disabled = false,
         config = null,
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun xcontext(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         focused = false,
         disabled = true,
         config = null,
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun context(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(
         TestNameBuilder.builder(name).build(),
         focused = false,
         xdisabled = false,
         this
      ) { DescribeSpecContainerScope(it) }

   fun xcontext(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(
         TestNameBuilder.builder(name).build(),
         focused = false,
         xdisabled = true,
         this
      ) { DescribeSpecContainerScope(it) }

   fun describe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         focused = false,
         disabled = false,
         null
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun fdescribe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         focused = true,
         disabled = false,
         null
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun xdescribe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         focused = false,
         disabled = true,
         null
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun describe(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         focused = false,
         xdisabled = false,
         this
      ) { DescribeSpecContainerScope(it) }

   fun fdescribe(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         focused = true,
         xdisabled = false,
         this
      ) { DescribeSpecContainerScope(it) }

   fun xdescribe(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         focused = false,
         xdisabled = true,
         this
      ) { DescribeSpecContainerScope(it) }

   fun it(name: String, test: suspend TestScope.() -> Unit) {
      addTest(
         testName = TestNameBuilder.builder(name).build(),
         focused = false,
         disabled = false,
         config = null,
         test = test
      )
   }

   fun fit(name: String, test: suspend TestScope.() -> Unit) {
      addTest(
         testName = TestNameBuilder.builder(name).build(),
         focused = true,
         disabled = false,
         config = null,
         test = test
      )
   }

   fun xit(name: String, test: suspend TestScope.() -> Unit) {
      addTest(
         testName = TestNameBuilder.builder(name).build(),
         focused = false,
         disabled = true,
         config = null,
         test = test
      )
   }
}
