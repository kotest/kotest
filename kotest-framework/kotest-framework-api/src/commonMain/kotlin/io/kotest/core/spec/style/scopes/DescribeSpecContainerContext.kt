package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.descriptors.append
import io.kotest.core.names.TestName
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

@Deprecated("This interface has been renamed to DescribeSpecContainerContext. Deprecated since 4.5.")
typealias DescribeScope = DescribeSpecContainerContext

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * describe("some test")
 *
 * or
 *
 * xdescribe("some disabled test")
 *
 * and
 *
 * it("some test")
 * it("some test").config(...)
 * xit("some test")
 * xit("some test").config(...)
 */
class DescribeSpecContainerContext(
   val testContext: TestContext,
) : AbstractContainerContext(testContext) {

   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> describe(name, test)
         TestType.Test -> it(name, test)
      }
   }

   /**
    * Registers a container test.
    */
   suspend fun context(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      registerContainer(TestName("Context: ", name, false), false, null) { DescribeSpecContainerContext(this).test() }
   }

   @ExperimentalKotest
   fun context(name: String): ContainerContextConfigBuilder<DescribeSpecContainerContext> =
      ContainerContextConfigBuilder(TestName(name), this, false) { DescribeSpecContainerContext(it) }

   /**
    * Registers a disabled container test.
    */
   suspend fun xcontext(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      registerContainer(TestName("Context: ", name, false), true, null) { DescribeSpecContainerContext(this).test() }
   }

   @ExperimentalKotest
   fun xcontext(name: String): ContainerContextConfigBuilder<DescribeSpecContainerContext> =
      ContainerContextConfigBuilder(TestName("Context: ", name, false), this, true) { DescribeSpecContainerContext(it) }

   /**
    * Registers a container test.
    */
   suspend fun describe(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      registerContainer(TestName("Describe: ", name, false), false, null) { DescribeSpecContainerContext(this).test() }
   }

   /**
    * Registers a container test.
    */
   suspend fun xdescribe(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      registerContainer(TestName("Describe: ", name, false), true, null) { DescribeSpecContainerContext(this).test() }
   }

   @ExperimentalKotest
   fun describe(name: String): ContainerContextConfigBuilder<DescribeSpecContainerContext> =
      ContainerContextConfigBuilder(
         TestName("Describe: ", name, false),
         this,
         false
      ) { DescribeSpecContainerContext(it) }

   @ExperimentalKotest
   fun xdescribe(name: String): ContainerContextConfigBuilder<DescribeSpecContainerContext> =
      ContainerContextConfigBuilder(
         TestName("Describe: ", name, false),
         this,
         true
      ) { DescribeSpecContainerContext(it) }

   suspend fun it(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.append(name))
      return TestWithConfigBuilder(
         TestName("It: ", name, false),
         this,
         xdisabled = false,
      )
   }

   suspend fun xit(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.append(name))
      return TestWithConfigBuilder(
         TestName("It: ", name, false),
         this,
         xdisabled = true,
      )
   }

   suspend fun it(name: String, test: suspend TestContext.() -> Unit) {
      registerTest(TestName(name), false, null) { DescribeSpecContainerContext(this).test() }
   }

   suspend fun xit(name: String, test: suspend TestContext.() -> Unit) {
      registerTest(TestName(name), true, null) { DescribeSpecContainerContext(this).test() }
   }
}
