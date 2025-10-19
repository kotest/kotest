package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
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
) : AbstractContainerScope(testScope) {

   suspend fun And(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addAnd(name, xmethod = TestXMethod.NONE, test)

   suspend fun and(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addAnd(name, xmethod = TestXMethod.NONE, test)

   suspend fun xand(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addAnd(name, xmethod = TestXMethod.DISABLED, test)

   suspend fun xAnd(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addAnd(name, xmethod = TestXMethod.DISABLED, test)

   private suspend fun addAnd(
      name: String,
      xmethod: TestXMethod,
      test: suspend BehaviorSpecGivenContainerScope.() -> Unit,
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("And: ").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = null
      ) {
         BehaviorSpecGivenContainerScope(this).test()
      }
   }

   suspend fun When(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addWhen(name, test, xmethod = TestXMethod.NONE)

   suspend fun `when`(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addWhen(name, test, xmethod = TestXMethod.NONE)

   suspend fun xwhen(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addWhen(name, test, xmethod = TestXMethod.DISABLED)

   suspend fun xWhen(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      addWhen(name, test, xmethod = TestXMethod.DISABLED)

   private suspend fun addWhen(
      name: String,
      test: suspend BehaviorSpecWhenContainerScope.() -> Unit,
      xmethod: TestXMethod
   ) {
      registerContainer(TestNameBuilder.builder(name).withPrefix("When: ").withDefaultAffixes().build(), xmethod = xmethod, null) {
         BehaviorSpecWhenContainerScope(this).test()
      }
   }

   fun Then(name: String) = TestWithConfigBuilder(
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
      this@BehaviorSpecGivenContainerScope,
      xmethod = TestXMethod.NONE
   )

   fun then(name: String) = TestWithConfigBuilder(
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
      this@BehaviorSpecGivenContainerScope,
      xmethod = TestXMethod.NONE
   )

   fun xthen(name: String) = TestWithConfigBuilder(
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
      this@BehaviorSpecGivenContainerScope,
      xmethod = TestXMethod.DISABLED
   )

   fun xThen(name: String) = TestWithConfigBuilder(
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
      this@BehaviorSpecGivenContainerScope,
      xmethod = TestXMethod.DISABLED
   )

   suspend fun Then(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xmethod = TestXMethod.NONE)
   suspend fun then(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xmethod = TestXMethod.NONE)
   suspend fun xthen(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xmethod = TestXMethod.DISABLED)
   suspend fun xThen(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xmethod = TestXMethod.DISABLED)

   private suspend fun addThen(name: String, test: suspend TestScope.() -> Unit, xmethod: TestXMethod) {
      registerTest(TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(), xmethod = xmethod, null, test)
   }
}
