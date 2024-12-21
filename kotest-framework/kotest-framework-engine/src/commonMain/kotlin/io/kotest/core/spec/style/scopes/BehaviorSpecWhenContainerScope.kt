package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestScope

/**
 * A context that allows tests to be registered using the syntax:
 *
 * ```
 * then("some test")
 * then("some test").config(...)
 * ```
 *
 * or disabled tests via:
 *
 * ```
 * xthen("some disabled test")
 * xthen("some disabled test").config(...)
 * ```
 */
@Suppress("FunctionName")
@KotestTestScope
class BehaviorSpecWhenContainerScope(val testScope: TestScope) :
   AbstractContainerScope<BehaviorSpecWhenContainerScope>(testScope) {

   suspend fun And(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addAnd(name, xdisabled = false, test)

   suspend fun and(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addAnd(name, xdisabled = false, test)

   suspend fun xand(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addAnd(name, xdisabled = true, test)

   suspend fun xAnd(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addAnd(name, xdisabled = true, test)

   private suspend fun addAnd(
      name: String,
      xdisabled: Boolean,
      test: suspend BehaviorSpecWhenContainerScope.() -> Unit,
   ) {
      registerContainer(TestName("And: ", name, true), xdisabled, null) {
         BehaviorSpecWhenContainerScope(this).test()
      }
   }

   fun then(name: String) =
      TestWithConfigBuilder(
         TestName("Then: ", name, true),
         this@BehaviorSpecWhenContainerScope,
         xdisabled = false
      )

   fun Then(name: String) =
      TestWithConfigBuilder(
         TestName("Then: ", name, true),
         this@BehaviorSpecWhenContainerScope,
         xdisabled = false
      )

   fun xthen(name: String) =
      TestWithConfigBuilder(
         TestName("Then: ", name, true),
         this@BehaviorSpecWhenContainerScope,
         xdisabled = true
      )

   fun xThen(name: String) =
      TestWithConfigBuilder(
         TestName("Then: ", name, true),
         this@BehaviorSpecWhenContainerScope,
         xdisabled = true
      )

   suspend fun Then(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xdisabled = false)
   suspend fun then(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xdisabled = false)
   suspend fun xthen(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xdisabled = true)
   suspend fun xThen(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xdisabled = true)

   private suspend fun addThen(name: String, test: suspend TestScope.() -> Unit, xdisabled: Boolean) {
      registerTest(TestName("Then: ", name, true), xdisabled, null, test)
   }

   /**
    * Registers tests inside the given [FunSpecContainerScope] for each element of [ts].
    * The test name will be generated from the given [nameFn] function.
    */
   override suspend fun <T> withData(
      nameFn: (T) -> String,
      ts: Iterable<T>,
      test: suspend BehaviorSpecWhenContainerScope.(T) -> Unit
   ) {
      ts.forEach { t -> addAnd(nameFn(t), false) { test(t) } }
   }
}