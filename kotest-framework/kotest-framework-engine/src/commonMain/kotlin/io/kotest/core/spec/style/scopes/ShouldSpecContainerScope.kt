package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
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
      context(name, false, test)
   }

   /**
    * Adds a disabled nested context scope to this scope.
    */
   suspend fun xcontext(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
      context(name, true, test)
   }

   private suspend fun context(name: String, xdisabled: Boolean, test: suspend ShouldSpecContainerScope.() -> Unit) {
      registerContainer(
         name = TestNameBuilder.builder(name).build(),
         disabled = xdisabled,
         config = null,
      ) { ShouldSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun context(name: String): ContainerWithConfigBuilder<ShouldSpecContainerScope> {
      return ContainerWithConfigBuilder(TestNameBuilder.builder(name).build(), this, false) {
         ShouldSpecContainerScope(
            it
         )
      }
   }

   @ExperimentalKotest
   fun xcontext(name: String): ContainerWithConfigBuilder<ShouldSpecContainerScope> {
      return ContainerWithConfigBuilder(
         TestNameBuilder.builder(name).build(),
         this,
         true
      ) { ShouldSpecContainerScope(it) }
   }

   suspend fun should(name: String): TestWithConfigBuilder {
      val testName = shouldName(name)
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(testName, this, false)
   }

   suspend fun xshould(name: String): TestWithConfigBuilder {
      val testName = shouldName(name)
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(testName, this, true)
   }

   suspend fun should(name: String, test: suspend TestScope.() -> Unit) {
      should(name, false, test)
   }

   suspend fun xshould(name: String, test: suspend TestScope.() -> Unit) {
      should(name, true, test)
   }

   private fun shouldName(name: String) =
      TestNameBuilder.builder(name).withPrefix("should ").withDefaultAffixes().build()

   private suspend fun should(name: String, xdisabled: Boolean, test: suspend TestScope.() -> Unit) {
      registerTest(
         name = shouldName(name),
         disabled = xdisabled,
         config = null,
         test = test
      )
   }
}
