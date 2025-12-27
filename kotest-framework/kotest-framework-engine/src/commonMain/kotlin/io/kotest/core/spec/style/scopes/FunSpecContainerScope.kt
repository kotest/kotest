package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
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
   testScope: TestScope,
) : AbstractContainerScope(testScope) {

   /**
    * Adds a 'context' container test as a child of the current test case.
    */
   suspend fun context(name: String, test: suspend FunSpecContainerScope.() -> Unit) {
      registerContainer(
         TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.NONE,
         null
      ) { FunSpecContainerScope(this).test() }
   }

   /**
    * Adds a disabled container test to this context.
    */
   suspend fun fcontext(name: String, test: suspend FunSpecContainerScope.() -> Unit) {
      registerContainer(
         TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.FOCUSED,
         null
      ) { FunSpecContainerScope(this).test() }
   }

   /**
    * Adds a disabled container test to this context.
    */
   suspend fun xcontext(name: String, test: suspend FunSpecContainerScope.() -> Unit) {
      registerContainer(
         TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.DISABLED,
         null
      ) { FunSpecContainerScope(this).test() }
   }

   /**
    * Adds a container test to this context expecting config.
    */
   @ExperimentalKotest
   fun context(name: String): ContainerWithConfigBuilder<FunSpecContainerScope> {
      return ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).build(),
         context = this,
         xmethod = TestXMethod.NONE,
         contextFn = { FunSpecContainerScope(it) }
      )
   }

   /**
    * Adds a disabled container to this context, expecting config.
    */
   fun fcontext(name: String): ContainerWithConfigBuilder<FunSpecContainerScope> {
      return ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).build(),
         context = this,
         xmethod = TestXMethod.FOCUSED,
      ) { FunSpecContainerScope(it) }
   }

   /**
    * Adds a disabled container to this context, expecting config.
    */
   fun xcontext(name: String): ContainerWithConfigBuilder<FunSpecContainerScope> {
      return ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).build(),
         context = this,
         xmethod = TestXMethod.DISABLED,
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
         xmethod = TestXMethod.NONE,
      )
   }

   /**
    * Adds a focused test case to this context, expecting config.
    */
   suspend fun ftest(name: String): TestWithConfigBuilder {
      val testName = TestNameBuilder.builder(name).build()
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xmethod = TestXMethod.FOCUSED,
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
         xmethod = TestXMethod.DISABLED,
      )
   }

   /**
    * Adds a test case to this context.
    */
   suspend fun test(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(name = TestNameBuilder.builder(name).build(), xmethod = TestXMethod.NONE, config = null, test = test)
   }

   /**
    * Adds a focused test case to this context.
    */
   suspend fun ftest(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(
         name = TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.FOCUSED,
         config = null,
         test = test
      )
   }

   /**
    * Adds a disabled test case to this context.
    */
   suspend fun xtest(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(
         name = TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.DISABLED,
         config = null,
         test = test
      )
   }
}
