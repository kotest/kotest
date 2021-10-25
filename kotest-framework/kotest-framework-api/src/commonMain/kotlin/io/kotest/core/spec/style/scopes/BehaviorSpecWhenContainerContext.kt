package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.TestContext

@Deprecated("This interface has been renamed to BehaviorSpecWhenContainerContext. Deprecated since 4.5.")
typealias WhenScope = BehaviorSpecWhenContainerContext

/**
 * A context that allows tests to be registered using the syntax:
 *
 * then("some test")
 * then("some test").config(...)
 *
 * or disabled tests via:
 *
 * xthen("some disabled test")
 * xthen("some disabled test").config(...)
 *
 */
@Suppress("FunctionName")
@KotestDsl
class BehaviorSpecWhenContainerContext(
   val testContext: TestContext,
) : AbstractContainerContext(testContext) {

   suspend fun And(name: String, test: suspend BehaviorSpecWhenContainerContext.() -> Unit) = addAnd(name, test, xdisabled = false)
   suspend fun and(name: String, test: suspend BehaviorSpecWhenContainerContext.() -> Unit) = addAnd(name, test, xdisabled = false)
   suspend fun xand(name: String, test: suspend BehaviorSpecWhenContainerContext.() -> Unit) = addAnd(name, test, xdisabled = true)
   suspend fun xAnd(name: String, test: suspend BehaviorSpecWhenContainerContext.() -> Unit) = addAnd(name, test, xdisabled = true)

   private suspend fun addAnd(name: String, test: suspend BehaviorSpecWhenContainerContext.() -> Unit, xdisabled: Boolean) {
      registerContainer(TestName("And: ", name, true), xdisabled, null) {
         BehaviorSpecWhenContainerContext(this).test()
      }
   }

   fun then(name: String) =
      TestWithConfigBuilder(
         TestName("Then: ", name, true),
         this@BehaviorSpecWhenContainerContext,
         xdisabled = false
      )

   fun Then(name: String) =
      TestWithConfigBuilder(
         TestName("Then: ", name, true),
         this@BehaviorSpecWhenContainerContext,
         xdisabled = false
      )

   fun xthen(name: String) =
      TestWithConfigBuilder(
         TestName("Then: ", name, true),
         this@BehaviorSpecWhenContainerContext,
         xdisabled = true
      )

   fun xThen(name: String) =
      TestWithConfigBuilder(
         TestName("Then: ", name, true),
         this@BehaviorSpecWhenContainerContext,
         xdisabled = true
      )

   suspend fun Then(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = false)
   suspend fun then(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = false)
   suspend fun xthen(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = true)
   suspend fun xThen(name: String, test: suspend TestContext.() -> Unit) = addThen(name, test, xdisabled = true)

   private suspend fun addThen(name: String, test: suspend TestContext.() -> Unit, xdisabled: Boolean) {
      registerTest(TestName("Then: ", name, true), xdisabled, null, test)
   }
}
