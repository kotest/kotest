package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestScope
import io.kotest.datatest.WithDataContainerRegistrar

/**
 * A context that allows tests to be registered using the syntax:
 *
 * context("some context")
 * test("some test")
 * test("some test").config(...)
 *
 */
@KotestTestScope
class FunSpecContainerScope(
   testScope: TestScope,
) : AbstractContainerScope(testScope), WithDataContainerRegistrar<FunSpecContainerScope> {

   /**
    * Adds a 'context' container test as a child of the current test case.
    */
   suspend fun context(name: String, test: suspend FunSpecContainerScope.() -> Unit) {
      registerContainer(TestNameBuilder.builder(name).build(), false, null) { FunSpecContainerScope(this).test() }
   }

   /**
    * Adds a disabled container test to this context.
    */
   suspend fun xcontext(name: String, test: suspend FunSpecContainerScope.() -> Unit) {
      registerContainer(TestNameBuilder.builder(name).build(), true, null) { FunSpecContainerScope(this).test() }
   }

   /**
    * Adds a container test to this context expecting config.
    */
   @ExperimentalKotest
   fun context(name: String): ContainerWithConfigBuilder<FunSpecContainerScope> {
      return ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).build(),
         context = this,
         xdisabled = false,
         contextFn = { FunSpecContainerScope(it) }
      )
   }

   /**
    * Adds a disabled container to this context, expecting config.
    */
   @ExperimentalKotest
   fun xcontext(name: String): ContainerWithConfigBuilder<FunSpecContainerScope> {
      return ContainerWithConfigBuilder(
         TestNameBuilder.builder(name).build(),
         this,
         true
      ) { FunSpecContainerScope(it) }
   }

   /**
    * Adds a test case to this context, expecting config.
    */
   suspend fun test(name: String): TestWithConfigBuilder {
      val testName = TestNameBuilder.builder(name).build()
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xdisabled = false,
      )
   }

   /**
    * Adds a disabled test case to this context, expecting config.
    */
   suspend fun xtest(name: String): TestWithConfigBuilder {
      val testName = TestNameBuilder.builder(name).build()
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xdisabled = true,
      )
   }

   /**
    * Adds a test case to this context.
    */
   suspend fun test(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(name = TestNameBuilder.builder(name).build(), disabled = false, config = null, test = test)
   }

   /**
    * Adds a disabled test case to this context.
    */
   suspend fun xtest(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(name = TestNameBuilder.builder(name).build(), disabled = true, config = null, test = test)
   }

   override suspend fun registerWithDataTest(
      name: String,
      test: suspend FunSpecContainerScope.() -> Unit
   ) {
      context(name) { test() }
   }
}
