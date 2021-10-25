package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.descriptors.append
import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.TestContext

@Deprecated("This interface has been renamed to ShouldSpecContainerContext. Deprecated since 4.5.")
typealias ShouldSpecContextScope = ShouldSpecContainerContext

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * context("some context")
 * should("some test")
 * should("some test").config(...)
 *
 */
@KotestDsl
class ShouldSpecContainerContext(
   val testContext: TestContext,
) : AbstractContainerContext(testContext) {

   /**
    * Adds a nested context scope to this scope.
    */
   suspend fun context(name: String, test: suspend ShouldSpecContainerContext.() -> Unit) {
      registerContainer(TestName(name), false, null) { ShouldSpecContainerContext(this).test() }
   }

   /**
    * Adds a disabled nested context scope to this scope.
    */
   suspend fun xcontext(name: String, test: suspend ShouldSpecContainerContext.() -> Unit) {
      registerContainer(TestName(name), true, null) { ShouldSpecContainerContext(this).test() }
   }

   @ExperimentalKotest
   fun context(name: String): ContainerContextConfigBuilder<ShouldSpecContainerContext> {
      return ContainerContextConfigBuilder(TestName(name), this, false) { ShouldSpecContainerContext(it) }
   }

   @ExperimentalKotest
   fun xcontext(name: String): ContainerContextConfigBuilder<ShouldSpecContainerContext> {
      return ContainerContextConfigBuilder(TestName(name), this, true) { ShouldSpecContainerContext(it) }
   }

   suspend fun should(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.append(name))
      return TestWithConfigBuilder(TestName("should ", name, false), this, false)
   }

   suspend fun xshould(name: String): TestWithConfigBuilder {
      TestDslState.startTest(testContext.testCase.descriptor.append(name))
      return TestWithConfigBuilder(TestName("should ", name, false), this, true)
   }

   suspend fun should(name: String, test: suspend TestContext.() -> Unit) {
      registerTest(TestName("should ", name, false), false, null, test)
   }

   suspend fun xshould(name: String, test: suspend TestContext.() -> Unit) {
      registerTest(TestName("should ", name, true), true, null, test)

   }
}
