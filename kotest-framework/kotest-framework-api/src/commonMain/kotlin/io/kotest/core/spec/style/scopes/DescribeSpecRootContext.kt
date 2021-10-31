package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.test.TestScope

@Deprecated("Renamed to DescribeSpecRootContext. Deprecated since 4.5.")
typealias DescribeSpecRootScope = DescribeSpecRootContext

/**
 * A context that allows root tests to be registered using the syntax:
 *
 * describe("some test")
 *
 * or
 *
 * xdescribe("some disabled test")
 */
interface DescribeSpecRootContext : RootContext {

   fun context(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(TestName("Context: ", name, false), false, null) { DescribeSpecContainerScope(this).test() }
   }

   fun xcontext(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(TestName("Context: ", name, false), true, null) { DescribeSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun context(name: String): RootContextConfigBuilder<DescribeSpecContainerScope> =
      RootContextConfigBuilder(TestName(name), xdisabled = false, this) { DescribeSpecContainerScope(it) }

   @ExperimentalKotest
   fun xcontext(name: String): RootContextConfigBuilder<DescribeSpecContainerScope> =
      RootContextConfigBuilder(TestName(name), xdisabled = true, this) { DescribeSpecContainerScope(it) }

   fun describe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestName("Describe: ", name, false),
         disabled = false,
         null
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun xdescribe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestName("Describe: ", name, false),
         disabled = true,
         null
      ) { DescribeSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun describe(name: String): RootContextConfigBuilder<DescribeSpecContainerScope> =
      RootContextConfigBuilder(
         TestName("Describe: ", name, false),
         xdisabled = false,
         this
      ) { DescribeSpecContainerScope(it) }

   @ExperimentalKotest
   fun xdescribe(name: String): RootContextConfigBuilder<DescribeSpecContainerScope> =
      RootContextConfigBuilder(
         TestName("Describe: ", name, false),
         xdisabled = true,
         this
      ) { DescribeSpecContainerScope(it) }

   fun it(name: String, test: suspend TestScope.() -> Unit) {
      addTest(TestName(name), false, null, test)
   }

   fun xit(name: String, test: suspend TestScope.() -> Unit) {
      addTest(TestName(name), true, null, test)
   }
}
