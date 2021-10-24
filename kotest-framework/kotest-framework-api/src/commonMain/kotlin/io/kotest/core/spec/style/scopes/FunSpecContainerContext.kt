package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.descriptors.append
import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

@Deprecated("This interface has been renamed to FunSpecContainerContext. Deprecated since 4.5.")
typealias FunSpecContextScope = FunSpecContainerContext

/**
 * A context that allows tests to be registered using the syntax:
 *
 * context("some context")
 * test("some test")
 * test("some test").config(...)
 *
 */
@KotestDsl
class FunSpecContainerContext(
   private val testContext: TestContext,
) : AbstractContainerContext(testContext) {

   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> context(name, test)
         TestType.Test -> test(name, test)
      }
   }

   /**
    * Adds a 'context' container test as a child of the current test case.
    */
   suspend fun context(name: String, test: suspend FunSpecContainerContext.() -> Unit) {
      registerContainer(TestName(name), false, null) { FunSpecContainerContext(this).test() }
   }

   /**
    * Adds a container test to this context expecting config.
    */
   @ExperimentalKotest
   fun context(name: String) {
      ContainerContextConfigBuilder(
         name = TestName(name),
         context = this,
         xdisabled = false,
         contextFn = { FunSpecContainerContext(it) }
      )
   }

   /**
    * Adds a disabled container test to this context.
    */
   suspend fun xcontext(name: String, test: suspend FunSpecContainerContext.() -> Unit) {
      registerContainer(TestName(name), true, null) { FunSpecContainerContext(this).test() }
   }

   /**
    * Adds a disabled container to this context, expecting config.
    */
   @ExperimentalKotest
   fun xcontext(name: String) {
      ContainerContextConfigBuilder(
         TestName(name),
         this,
         true
      ) { FunSpecContainerContext(it) }
   }

   /**
    * Adds a test case to this context, expecting config.
    */
   suspend fun test(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.append(name))
      return TestWithConfigBuilder(
         name = TestName(name),
         context = this,
         xdisabled = true,
      )
   }

   /**
    * Adds a disabled test case to this context, expecting config.
    */
   suspend fun xtest(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.append(name))
      return TestWithConfigBuilder(
         name = TestName(name),
         context = this,
         xdisabled = true,
      )
   }

   /**
    * Adds a test case to this context.
    */
   suspend fun test(name: String, test: suspend TestContext.() -> Unit) {
      registerTest(TestName(name), false, null, test)
   }

   /**
    * Adds a disabled test case to this context.
    */
   suspend fun xtest(name: String, test: suspend TestContext.() -> Unit) {
      registerTest(TestName(name), true, null, test)
   }
}
