package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.test.TestContext

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

   fun context(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      addContainer(TestName("Context: ", name, false), false, null) { DescribeSpecContainerContext(this).test() }
   }

   fun xcontext(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      addContainer(TestName("Context: ", name, false), true, null) { DescribeSpecContainerContext(this).test() }
   }

   @ExperimentalKotest
   fun context(name: String): RootContextConfigBuilder<DescribeSpecContainerContext> =
      RootContextConfigBuilder(TestName(name), false, this) { DescribeSpecContainerContext(it) }

   @ExperimentalKotest
   fun xcontext(name: String): RootContextConfigBuilder<DescribeSpecContainerContext> =
      RootContextConfigBuilder(TestName(name), true, this) { DescribeSpecContainerContext(it) }

   fun describe(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      addTest(TestName("Describe: ", name, false), true, null) { DescribeSpecContainerContext(this).test() }
   }

   fun xdescribe(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      addTest(TestName("Describe: ", name, false), true, null) { DescribeSpecContainerContext(this).test() }
   }

   @ExperimentalKotest
   fun describe(name: String): RootContextConfigBuilder<DescribeSpecContainerContext> =
      RootContextConfigBuilder(TestName("Describe: ", name, false), false, this) { DescribeSpecContainerContext(it) }

   @ExperimentalKotest
   fun xdescribe(name: String): RootContextConfigBuilder<DescribeSpecContainerContext> =
      RootContextConfigBuilder(TestName("Describe: ", name, false), true, this) { DescribeSpecContainerContext(it) }

   fun it(name: String, test: suspend TestContext.() -> Unit) {
      addTest(TestName(name), false, null, test)
   }

   fun xit(name: String, test: suspend TestContext.() -> Unit) {
      addTest(TestName(name), true, null, test)
   }
}
