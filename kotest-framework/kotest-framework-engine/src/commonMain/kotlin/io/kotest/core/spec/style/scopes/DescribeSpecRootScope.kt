package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
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
      addContainer(TestNameBuilder.builder(name).withPrefix("Context: ").build(), false, null) { DescribeSpecContainerScope(this).test() }
   }

   fun xcontext(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(TestNameBuilder.builder(name).withPrefix("Context: ").build(), true, null) { DescribeSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun context(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(TestNameBuilder.builder(name).build(), xdisabled = false, this) { DescribeSpecContainerScope(it) }

   @ExperimentalKotest
   fun xcontext(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(TestNameBuilder.builder(name).build(), xdisabled = true, this) { DescribeSpecContainerScope(it) }

   fun describe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         disabled = false,
         null
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun xdescribe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         disabled = true,
         null
      ) { DescribeSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun describe(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         xdisabled = false,
         this
      ) { DescribeSpecContainerScope(it) }

   @ExperimentalKotest
   fun xdescribe(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         xdisabled = true,
         this
      ) { DescribeSpecContainerScope(it) }

   fun it(name: String, test: suspend TestScope.() -> Unit) {
      addTest(TestNameBuilder.builder(name).build(), false, null, test)
   }

   fun xit(name: String, test: suspend TestScope.() -> Unit) {
      addTest(TestNameBuilder.builder(name).build(), true, null, test)
   }
}
