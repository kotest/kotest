package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
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
      context(name = name, disabled = false, test = test)
   }

   /**
    * Registers a disabled container test.
    */
   suspend fun xcontext(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      context(name = name, disabled = true, test = test)
   }

   private suspend fun context(
      name: String,
      disabled: Boolean,
      test: suspend DescribeSpecContainerScope.() -> Unit
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         disabled = disabled,
         config = null
      ) { DescribeSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun context(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(TestNameBuilder.builder(name).build(), this, false) { DescribeSpecContainerScope(it) }

   @ExperimentalKotest
   fun xcontext(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         this,
         true
      ) { DescribeSpecContainerScope(it) }

   /**
    * Registers a container test.
    */
   suspend fun describe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      describe(name = name, xdisabled = false, test = test)
   }

   /**
    * Registers a container test.
    */
   suspend fun xdescribe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      describe(name = name, xdisabled = true, test = test)
   }

   private suspend fun describe(
      name: String,
      xdisabled: Boolean,
      test: suspend DescribeSpecContainerScope.() -> Unit
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         disabled = xdisabled,
         config = null,
      ) { DescribeSpecContainerScope(this).test() }
   }


   @ExperimentalKotest
   fun describe(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         this,
         false
      ) { DescribeSpecContainerScope(it) }

   @ExperimentalKotest
   fun xdescribe(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         TestNameBuilder.builder(name).withPrefix("Describe: ").build(),
         this,
         true
      ) { DescribeSpecContainerScope(it) }

   suspend fun it(name: String): TestWithConfigBuilder {
      val testName = TestNameBuilder.builder(name).withPrefix("It: ").build()
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xdisabled = false,
      )
   }

   suspend fun xit(name: String): TestWithConfigBuilder {
      val testName = TestNameBuilder.builder(name).withPrefix("It: ").build()
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         testName,
         this,
         xdisabled = true,
      )
   }

   suspend fun it(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(
         name = TestNameBuilder.builder(name).build(),
         disabled = false,
         config = null
      ) { DescribeSpecContainerScope(this).test() }
   }

   suspend fun xit(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(
         name = TestNameBuilder.builder(name).build(),
         disabled = true,
         config = null
      ) { DescribeSpecContainerScope(this).test() }
   }
}
