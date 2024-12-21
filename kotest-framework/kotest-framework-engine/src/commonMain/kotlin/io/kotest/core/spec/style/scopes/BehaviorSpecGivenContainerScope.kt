package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestScope

/**
 * A context that allows tests to be registered using the syntax:
 *
 * ```
 * when("some test")
 * when("some test").config(...)
 * xwhen("some disabled test")
 * xwhen("some disabled test").config(...)
 * ```
 *
 * and
 *
 * ```
 * then("some test")
 * then("some test").config(...)
 * xthen("some disabled test").config(...)
 * xthen("some disabled test").config(...)
 * ```
 */
@Suppress("FunctionName")
@KotestTestScope
class BehaviorSpecGivenContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope<BehaviorSpecGivenContainerScope>(testScope) {

   suspend fun And(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addAnd(name, xdisabled = false, test)

   suspend fun and(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addAnd(name, xdisabled = false, test)

   suspend fun xand(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addAnd(name, xdisabled = true, test)

   suspend fun xAnd(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addAnd(name, xdisabled = true, test)

   private suspend fun addAnd(
      name: String,
      xdisabled: Boolean,
      test: suspend BehaviorSpecGivenContainerScope.() -> Unit,
   ) {
      registerContainer(TestName("And: ", name, true), xdisabled, null) {
         BehaviorSpecGivenContainerScope(this).test()
      }
   }

   suspend fun When(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addWhen(name, test, xdisabled = false)

   suspend fun `when`(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addWhen(name, test, xdisabled = false)

   suspend fun xwhen(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addWhen(name, test, xdisabled = true)

   suspend fun xWhen(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addWhen(name, test, xdisabled = true)

   private suspend fun addWhen(
      name: String,
      test: suspend BehaviorSpecWhenContainerScope.() -> Unit,
      xdisabled: Boolean
   ) {
      registerContainer(TestName("When: ", name, true), disabled = xdisabled, null) {
         BehaviorSpecWhenContainerScope(this).test()
      }
   }

   fun Then(name: String) = TestWithConfigBuilder(
      TestName("Then: ", name, true),
      this@BehaviorSpecGivenContainerScope,
      xdisabled = false
   )

   fun then(name: String) = TestWithConfigBuilder(
      TestName("Then: ", name, true),
      this@BehaviorSpecGivenContainerScope,
      xdisabled = false
   )

   fun xthen(name: String) = TestWithConfigBuilder(
      TestName("Then: ", name, true),
      this@BehaviorSpecGivenContainerScope,
      xdisabled = true
   )

   fun xThen(name: String) = TestWithConfigBuilder(
      TestName("Then: ", name, true),
      this@BehaviorSpecGivenContainerScope,
      xdisabled = true
   )

   suspend fun Then(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xdisabled = false)
   suspend fun then(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xdisabled = false)
   suspend fun xthen(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xdisabled = true)
   suspend fun xThen(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xdisabled = true)

   private suspend fun addThen(name: String, test: suspend TestScope.() -> Unit, xdisabled: Boolean) {
      registerTest(TestName("Then: ", name, true), disabled = xdisabled, null, test)
   }

   // data-test DSL follows

   /**
    * Registers tests inside the given [FunSpecContainerScope] for each element of [ts].
    * The test name will be generated from the given [nameFn] function.
    */
   override suspend fun <T> withData(
      nameFn: (T) -> String,
      ts: Iterable<T>,
      test: suspend BehaviorSpecGivenContainerScope.(T) -> Unit
   ) {
      ts.forEach { t -> addAnd(nameFn(t), false) { test(t) } }
   }
}
