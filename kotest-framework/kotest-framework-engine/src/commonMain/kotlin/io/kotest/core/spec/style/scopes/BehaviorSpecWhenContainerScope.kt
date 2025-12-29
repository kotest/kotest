package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
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
   AbstractContainerScope(testScope) {

   suspend fun And(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      and(name, xmethod = TestXMethod.NONE, test)

   suspend fun and(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      and(name, xmethod = TestXMethod.NONE, test)

   suspend fun fand(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      and(name, xmethod = TestXMethod.FOCUSED, test)

   suspend fun fAnd(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      and(name, xmethod = TestXMethod.FOCUSED, test)

   suspend fun xand(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      and(name, xmethod = TestXMethod.DISABLED, test)

   suspend fun xAnd(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      and(name, xmethod = TestXMethod.DISABLED, test)

   private suspend fun and(
      name: String,
      xmethod: TestXMethod,
      test: suspend BehaviorSpecWhenContainerScope.() -> Unit,
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("And: ").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = null
      ) {
         BehaviorSpecWhenContainerScope(this).test()
      }
   }

   fun then(name: String) = TestWithConfigBuilder(
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
      this@BehaviorSpecWhenContainerScope,
      xmethod = TestXMethod.NONE,
   )

   fun Then(name: String) = TestWithConfigBuilder(
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
      this@BehaviorSpecWhenContainerScope,
      xmethod = TestXMethod.NONE,
   )

   fun fthen(name: String) = TestWithConfigBuilder(
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
      this@BehaviorSpecWhenContainerScope,
      xmethod = TestXMethod.FOCUSED,
   )

   fun fThen(name: String) = TestWithConfigBuilder(
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
      this@BehaviorSpecWhenContainerScope,
      xmethod = TestXMethod.FOCUSED,
   )

   fun xthen(name: String) = TestWithConfigBuilder(
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
      this@BehaviorSpecWhenContainerScope,
      xmethod = TestXMethod.DISABLED,
   )

   fun xThen(name: String) = TestWithConfigBuilder(
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
      this@BehaviorSpecWhenContainerScope,
      xmethod = TestXMethod.DISABLED,
   )

   suspend fun Then(name: String, test: suspend TestScope.() -> Unit) =
      then(name, test, xmethod = TestXMethod.NONE)

   suspend fun then(name: String, test: suspend TestScope.() -> Unit) =
      then(name, test, xmethod = TestXMethod.NONE)

   suspend fun xthen(name: String, test: suspend TestScope.() -> Unit) =
      then(name, test, xmethod = TestXMethod.DISABLED)

   suspend fun xThen(name: String, test: suspend TestScope.() -> Unit) =
      then(name, test, xmethod = TestXMethod.DISABLED)

   private suspend fun then(name: String, test: suspend TestScope.() -> Unit, xmethod: TestXMethod) {
      registerTest(
         name = TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = null,
         test = test
      )
   }
}
