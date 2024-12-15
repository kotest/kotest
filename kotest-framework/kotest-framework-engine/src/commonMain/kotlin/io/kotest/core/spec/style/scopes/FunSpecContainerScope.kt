package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestScope

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
   private val testScope: TestScope,
) : AbstractContainerScope(testScope) {

   /**
    * Adds a 'context' container test as a child of the current test case.
    */
   suspend fun context(name: String, test: suspend FunSpecContainerScope.() -> Unit) {
      registerContainer(TestName(name), false, null) { FunSpecContainerScope(this).test() }
   }

   /**
    * Adds a container test to this context expecting config.
    */
   @ExperimentalKotest
   fun context(name: String): ContainerWithConfigBuilder<FunSpecContainerScope> {
      return ContainerWithConfigBuilder(
         name = TestName(name),
         context = this,
         xdisabled = false,
         contextFn = { FunSpecContainerScope(it) }
      )
   }

   /**
    * Adds a disabled container test to this context.
    */
   suspend fun xcontext(name: String, test: suspend FunSpecContainerScope.() -> Unit) {
      registerContainer(TestName(name), true, null) { FunSpecContainerScope(this).test() }
   }

   /**
    * Adds a disabled container to this context, expecting config.
    */
   @ExperimentalKotest
   fun xcontext(name: String): ContainerWithConfigBuilder<FunSpecContainerScope> {
      return ContainerWithConfigBuilder(
         TestName(name),
         this,
         true
      ) { FunSpecContainerScope(it) }
   }

   /**
    * Adds a test case to this context, expecting config.
    */
   suspend fun test(name: String): TestWithConfigBuilder {
     TestDslState.startTest(name)
      return TestWithConfigBuilder(
         name = TestName(name),
         context = this,
         xdisabled = false,
      )
   }

   /**
    * Adds a disabled test case to this context, expecting config.
    */
   suspend fun xtest(name: String): TestWithConfigBuilder {
     TestDslState.startTest(name)
      return TestWithConfigBuilder(
         name = TestName(name),
         context = this,
         xdisabled = true,
      )
   }

   /**
    * Adds a test case to this context.
    */
   suspend fun test(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(TestName(name), false, null, test)
   }

   /**
    * Adds a disabled test case to this context.
    */
   suspend fun xtest(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(TestName(name), true, null, test)
   }
}
