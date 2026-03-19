package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope

/**
 * A scope that allows tests to be registered using the syntax:
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
 *
 * and
 *
 * ```
 * it("some test")
 * it("some test").config(...)
 * xit("some test")
 * xit("some test").config(...)
 * ```
 */
@KotestTestScope
class DescribeSpecContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope) {

   /**
    * Registers a container test.
    */
   suspend fun context(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      context(name = name, xmethod = TestXMethod.NONE, test = test)
   }

   suspend fun fcontext(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      context(name = name, xmethod = TestXMethod.FOCUSED, test = test)
   }

   /**
    * Registers a disabled container test.
    */
   suspend fun xcontext(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      context(name = name, xmethod = TestXMethod.DISABLED, test = test)
   }

   private suspend fun context(
      name: String,
      xmethod: TestXMethod,
      test: suspend DescribeSpecContainerScope.() -> Unit
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         xmethod = xmethod,
         config = null
      ) { DescribeSpecContainerScope(this).test() }
   }

   fun context(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).build(),
         context = this,
         xmethod = TestXMethod.NONE,
      ) { DescribeSpecContainerScope(it) }

   fun fcontext(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).build(),
         context = this,
         xmethod = TestXMethod.FOCUSED,
      ) { DescribeSpecContainerScope(it) }

   fun xcontext(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         context = this,
         xmethod = TestXMethod.DISABLED,
      ) { DescribeSpecContainerScope(it) }

   /**
    * Registers a container test.
    */
   suspend fun describe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      describe(name = name, xmethod = TestXMethod.NONE, test = test)
   }

   suspend fun fdescribe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      describe(name = name, xmethod = TestXMethod.FOCUSED, test = test)
   }

   /**
    * Registers a container test.
    */
   suspend fun xdescribe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      describe(name = name, xmethod = TestXMethod.DISABLED, test = test)
   }

   private suspend fun describe(
      name: String,
      xmethod: TestXMethod,
      test: suspend DescribeSpecContainerScope.() -> Unit
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         xmethod = xmethod,
         config = null,
      ) { DescribeSpecContainerScope(this).test() }
   }


   fun describe(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         context = this,
         xmethod = TestXMethod.NONE,
      ) { DescribeSpecContainerScope(it) }

   fun fdescribe(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         context = this,
         xmethod = TestXMethod.FOCUSED,
      ) { DescribeSpecContainerScope(it) }

   fun xdescribe(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         context = this,
         xmethod = TestXMethod.DISABLED,
      ) { DescribeSpecContainerScope(it) }

   suspend fun it(name: String): TestWithConfigBuilder {
      val testName = TestNameBuilder.builder(name).withPrefix("It: ").build()
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xmethod = TestXMethod.NONE,
      )
   }

   suspend fun fit(name: String): TestWithConfigBuilder {
      val testName = TestNameBuilder.builder(name).withPrefix("It: ").build()
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         testName,
         this,
         xmethod = TestXMethod.FOCUSED,
      )
   }

   suspend fun xit(name: String): TestWithConfigBuilder {
      val testName = TestNameBuilder.builder(name).withPrefix("It: ").build()
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         testName,
         this,
         xmethod = TestXMethod.DISABLED,
      )
   }

   suspend fun it(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(
         name = TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.NONE,
         config = null
      ) { DescribeSpecContainerScope(this).test() }
   }

   suspend fun fit(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(
         name = TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.FOCUSED,
         config = null
      ) { DescribeSpecContainerScope(this).test() }
   }

   suspend fun xit(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(
         name = TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.DISABLED,
         config = null
      ) { DescribeSpecContainerScope(this).test() }
   }
}
