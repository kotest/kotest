package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.TestXMethod
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
         xmethod = TestXMethod.NONE,
         config = null,
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun fcontext(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         xmethod = TestXMethod.FOCUSED,
         config = null,
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun xcontext(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         xmethod = TestXMethod.DISABLED,
         config = null,
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun context(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(
         TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.NONE,
         this
      ) { DescribeSpecContainerScope(it) }

   fun fcontext(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(
         TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.FOCUSED,
         this
      ) { DescribeSpecContainerScope(it) }

   fun xcontext(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(
         TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.DISABLED,
         this
      ) { DescribeSpecContainerScope(it) }

   fun describe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         xmethod = TestXMethod.NONE,
         null
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun fdescribe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         xmethod = TestXMethod.FOCUSED,
         null
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun xdescribe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      addContainer(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         xmethod = TestXMethod.DISABLED,
         null
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun describe(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         xmethod = TestXMethod.NONE,
         this
      ) { DescribeSpecContainerScope(it) }

   fun fdescribe(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         xmethod = TestXMethod.FOCUSED,
         this
      ) { DescribeSpecContainerScope(it) }

   fun xdescribe(name: String): RootContainerWithConfigBuilder<DescribeSpecContainerScope> =
      RootContainerWithConfigBuilder(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         xmethod = TestXMethod.DISABLED,
         this
      ) { DescribeSpecContainerScope(it) }

   fun it(name: String, test: suspend TestScope.() -> Unit) {
      addTest(
         testName = TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.NONE,
         config = null,
         test = test
      )
   }

   fun fit(name: String, test: suspend TestScope.() -> Unit) {
      addTest(
         testName = TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.FOCUSED,
         config = null,
         test = test
      )
   }

   fun xit(name: String, test: suspend TestScope.() -> Unit) {
      addTest(
         testName = TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.DISABLED,
         config = null,
         test = test
      )
   }
}
