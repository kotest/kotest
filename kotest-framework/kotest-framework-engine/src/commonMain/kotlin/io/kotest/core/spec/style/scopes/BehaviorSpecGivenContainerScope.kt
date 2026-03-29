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
      registerTest(
         TestDefinitionBuilder.builder(andName(name), TestType.Test)
            .withXmethod(xmethod)
            .build { BehaviorSpecGivenContainerScope(this).test() }
      )
   }

   fun And(name: String) =
      addAnd(name, xmethod = TestXMethod.NONE)

   fun and(name: String) =
      addAnd(name, xmethod = TestXMethod.NONE)

   fun xand(name: String) =
      addAnd(name, xmethod = TestXMethod.DISABLED)

   fun xAnd(name: String) =
      addAnd(name, xmethod = TestXMethod.DISABLED)

   private fun addAnd(
      name: String,
      xmethod: TestXMethod
   ): ContainerWithConfigBuilder<BehaviorSpecGivenContainerScope> {
      return ContainerWithConfigBuilder(
         name = andName(name),
         context = this,
         xmethod = xmethod
      ) { BehaviorSpecGivenContainerScope(it) }
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
      registerTest(
         TestDefinitionBuilder.builder(whenName(name), TestType.Test)
            .withXmethod(xmethod)
            .build { BehaviorSpecWhenContainerScope(this).test() }
      )
   }

   fun When(name: String) =
      addWhen(name, xmethod = TestXMethod.NONE)

   fun `when`(name: String) =
      addWhen(name, xmethod = TestXMethod.NONE)

   fun xwhen(name: String) =
      addWhen(name, xmethod = TestXMethod.DISABLED)

   fun xWhen(name: String) =
      addWhen(name, xmethod = TestXMethod.DISABLED)

   private fun addWhen(
      name: String,
      xmethod: TestXMethod
   ): ContainerWithConfigBuilder<BehaviorSpecWhenContainerScope> {
      return ContainerWithConfigBuilder(
         name = whenName(name),
         context = this,
         xmethod = xmethod
      ) { BehaviorSpecWhenContainerScope(it) }
   }

   fun Then(name: String) = TestWithConfigBuilder(
      thenName(name),
      this@BehaviorSpecGivenContainerScope,
      xmethod = TestXMethod.NONE
   )

   fun then(name: String) = TestWithConfigBuilder(
      thenName(name),
      this@BehaviorSpecGivenContainerScope,
      xmethod = TestXMethod.NONE
   )

   fun xthen(name: String) = TestWithConfigBuilder(
      thenName(name),
      this@BehaviorSpecGivenContainerScope,
      xmethod = TestXMethod.DISABLED
   )

   fun xThen(name: String) = TestWithConfigBuilder(
      thenName(name),
      this@BehaviorSpecGivenContainerScope,
      xmethod = TestXMethod.DISABLED
   )

   suspend fun Then(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xmethod = TestXMethod.NONE)
   suspend fun then(name: String, test: suspend TestScope.() -> Unit) = addThen(name, test, xmethod = TestXMethod.NONE)
   suspend fun xthen(name: String, test: suspend TestScope.() -> Unit) =
      addThen(name, test, xmethod = TestXMethod.DISABLED)

   suspend fun xThen(name: String, test: suspend TestScope.() -> Unit) =
      addThen(name, test, xmethod = TestXMethod.DISABLED)

   private suspend fun addThen(name: String, test: suspend TestScope.() -> Unit, xmethod: TestXMethod) {
      registerTest(
         TestDefinitionBuilder.builder(thenName(name), TestType.Test)
            .withXmethod(xmethod)
            .build(test)
      )
   }

   private fun whenName(name: String): TestName =
      TestNameBuilder.builder(name).withPrefix("When: ").withDefaultAffixes().build()

   private fun andName(name: String): TestName =
      TestNameBuilder.builder(name).withPrefix("And: ").withDefaultAffixes().build()

   private fun thenName(name: String): TestName =
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build()
}
