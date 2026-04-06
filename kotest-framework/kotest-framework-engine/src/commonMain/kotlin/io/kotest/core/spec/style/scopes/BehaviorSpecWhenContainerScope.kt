package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType

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

   suspend fun xand(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      and(name, xmethod = TestXMethod.DISABLED, test)

   suspend fun xAnd(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      and(name, xmethod = TestXMethod.DISABLED, test)

   private suspend fun and(
      name: String,
      xmethod: TestXMethod,
      test: suspend BehaviorSpecWhenContainerScope.() -> Unit,
   ) {
      registerTest(
         TestDefinitionBuilder
            .builder(andName(name), TestType.Container)
            .withXmethod(xmethod)
            .build { BehaviorSpecWhenContainerScope(this).test() }
      )
   }

   fun And(name: String) = addAnd(name, xmethod = TestXMethod.NONE)

   fun and(name: String) = addAnd(name, xmethod = TestXMethod.NONE)

   fun xand(name: String) = addAnd(name, xmethod = TestXMethod.DISABLED)

   fun xAnd(name: String) = addAnd(name, xmethod = TestXMethod.DISABLED)

   private fun addAnd(
      name: String,
      xmethod: TestXMethod
   ): ContainerWithConfigBuilder<BehaviorSpecWhenContainerScope> {
      return ContainerWithConfigBuilder(
         name = andName(name),
         context = this,
         xmethod = xmethod
      ) { BehaviorSpecWhenContainerScope(it) }
   }

   fun then(name: String) = TestWithConfigBuilder(
      thenName(name),
      this@BehaviorSpecWhenContainerScope,
      xmethod = TestXMethod.NONE,
   )

   fun Then(name: String) = TestWithConfigBuilder(
      thenName(name),
      this@BehaviorSpecWhenContainerScope,
      xmethod = TestXMethod.NONE,
   )

   fun xthen(name: String) = TestWithConfigBuilder(
      thenName(name),
      this@BehaviorSpecWhenContainerScope,
      xmethod = TestXMethod.DISABLED,
   )

   fun xThen(name: String) = TestWithConfigBuilder(
      thenName(name),
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
         TestDefinitionBuilder
            .builder(thenName(name), TestType.Test)
            .withXmethod(xmethod)
            .build(test)
      )
   }

   private fun andName(name: String): TestName =
      TestNameBuilder.builder(name).withPrefix("And: ").withDefaultAffixes().build()

   private fun thenName(name: String): TestName =
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build()
}
