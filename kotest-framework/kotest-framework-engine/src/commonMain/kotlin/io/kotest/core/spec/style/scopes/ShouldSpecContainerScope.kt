package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * ```
 * context("some context")
 * should("some test")
 * should("some test").config(...)
 * ```
 */
@KotestTestScope
class ShouldSpecContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope) {

   /**
    * Adds a nested context scope to this scope.
    */
   suspend fun context(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
      context(name, xmethod = TestXMethod.NONE, test)
   }

   /**
    * Adds a focused nested context scope to this scope.
    */
   suspend fun fcontext(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
      context(name, xmethod = TestXMethod.FOCUSED, test)
   }

   /**
    * Adds a disabled nested context scope to this scope.
    */
   suspend fun xcontext(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
      context(name, xmethod = TestXMethod.DISABLED, test)
   }

   private suspend fun context(name: String, xmethod: TestXMethod, test: suspend ShouldSpecContainerScope.() -> Unit) {
      registerContainer(
         name = TestNameBuilder.builder(name).build(),
         xmethod = xmethod,
         config = null,
      ) { ShouldSpecContainerScope(this).test() }
   }

   fun context(name: String): ContainerWithConfigBuilder<ShouldSpecContainerScope> {
      return ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).build(),
         context = this,
         xmethod = TestXMethod.NONE
      ) {
         ShouldSpecContainerScope(it)
      }
   }

   fun fcontext(name: String): ContainerWithConfigBuilder<ShouldSpecContainerScope> {
      return ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).build(),
         context = this,
         xmethod = TestXMethod.FOCUSED,
      ) { ShouldSpecContainerScope(it) }
   }

   fun xcontext(name: String): ContainerWithConfigBuilder<ShouldSpecContainerScope> {
      return ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).build(),
         context = this,
         xmethod = TestXMethod.DISABLED,
      ) { ShouldSpecContainerScope(it) }
   }

   suspend fun should(name: String): TestWithConfigBuilder {
      val testName = shouldName(name)
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(name = testName, context = this, xmethod = TestXMethod.NONE)
   }

   suspend fun fshould(name: String): TestWithConfigBuilder {
      val testName = shouldName(name)
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(name = testName, context = this, xmethod = TestXMethod.FOCUSED)
   }

   suspend fun xshould(name: String): TestWithConfigBuilder {
      val testName = shouldName(name)
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(name = testName, context = this, xmethod = TestXMethod.DISABLED)
   }

   suspend fun should(name: String, test: suspend TestScope.() -> Unit) {
      should(name, xmethod = TestXMethod.NONE, test)
   }

   suspend fun fshould(name: String, test: suspend TestScope.() -> Unit) {
      should(name, xmethod = TestXMethod.FOCUSED, test)
   }

   suspend fun xshould(name: String, test: suspend TestScope.() -> Unit) {
      should(name, xmethod = TestXMethod.DISABLED, test)
   }

   private fun shouldName(name: String) =
      TestNameBuilder.builder(name).withPrefix("should ").withDefaultAffixes().build()

   private suspend fun should(name: String, xmethod: TestXMethod, test: suspend TestScope.() -> Unit) {
      registerTest(
         name = shouldName(name),
         xmethod = xmethod,
         config = null,
         test = test
      )
   }
}
