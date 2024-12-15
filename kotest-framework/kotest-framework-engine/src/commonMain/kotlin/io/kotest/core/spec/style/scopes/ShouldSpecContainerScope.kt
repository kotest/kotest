package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
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
      registerContainer(TestName(name), false, null) { ShouldSpecContainerScope(this).test() }
   }

   /**
    * Adds a disabled nested context scope to this scope.
    */
   suspend fun xcontext(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
      registerContainer(TestName(name), true, null) { ShouldSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun context(name: String): ContainerWithConfigBuilder<ShouldSpecContainerScope> {
      return ContainerWithConfigBuilder(TestName(name), this, false) { ShouldSpecContainerScope(it) }
   }

   @ExperimentalKotest
   fun xcontext(name: String): ContainerWithConfigBuilder<ShouldSpecContainerScope> {
      return ContainerWithConfigBuilder(TestName(name), this, true) { ShouldSpecContainerScope(it) }
   }

   suspend fun should(name: String): TestWithConfigBuilder {
     TestDslState.startTest(name)
      return TestWithConfigBuilder(TestName("should ", name, false), this, false)
   }

   suspend fun xshould(name: String): TestWithConfigBuilder {
     TestDslState.startTest(name)
      return TestWithConfigBuilder(TestName("should ", name, false), this, true)
   }

   suspend fun should(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(TestName("should ", name, false), false, null, test)
   }

   suspend fun xshould(name: String, test: suspend TestScope.() -> Unit) {
      registerTest(TestName("should ", name, true), true, null, test)

   }
}
