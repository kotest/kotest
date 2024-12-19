package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
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
      registerContainer(TestName("Context: ", name, false), false, null) { DescribeSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun context(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(TestName(name), this, false) { DescribeSpecContainerScope(it) }

   /**
    * Registers a disabled container test.
    */
   suspend fun xcontext(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      registerContainer(TestName("Context: ", name, false), true, null) { DescribeSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun xcontext(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(TestName("Context: ", name, false), this, true) { DescribeSpecContainerScope(it) }

   /**
    * Registers a container test.
    */
   suspend fun describe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      registerContainer(TestName("Describe: ", name, false), false, null) { DescribeSpecContainerScope(this).test() }
   }

   /**
    * Registers a container test.
    */
   suspend fun xdescribe(name: String, test: suspend DescribeSpecContainerScope.() -> Unit) {
      registerContainer(TestName("Describe: ", name, false), true, null) { DescribeSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun describe(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         TestName("Describe: ", name, false),
         this,
         false
      ) { DescribeSpecContainerScope(it) }

   @ExperimentalKotest
   fun xdescribe(name: String): ContainerWithConfigBuilder<DescribeSpecContainerScope> =
      ContainerWithConfigBuilder(
         TestName("Describe: ", name, false),
         this,
         true
      ) { DescribeSpecContainerScope(it) }

   suspend fun it(name: String): TestWithConfigBuilder {
     TestDslState.startTest(name)
      return TestWithConfigBuilder(
         TestName("It: ", name, false),
         this,
         xdisabled = false,
      )
   }

   suspend fun xit(name: String): TestWithConfigBuilder {
     TestDslState.startTest(name)
      return TestWithConfigBuilder(
         TestName("It: ", name, false),
         this,
         xdisabled = true,
      )
   }

   suspend fun it(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(TestName(name), false, null) { DescribeSpecContainerScope(this).test() }
   }

   suspend fun xit(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(TestName(name), true, null) { DescribeSpecContainerScope(this).test() }
   }
}
