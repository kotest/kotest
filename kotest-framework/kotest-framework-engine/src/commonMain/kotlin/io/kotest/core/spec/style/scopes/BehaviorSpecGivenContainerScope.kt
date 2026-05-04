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

   suspend fun Given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addGiven(name, xmethod = TestXMethod.NONE, test)

   suspend fun given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addGiven(name, xmethod = TestXMethod.NONE, test)

   suspend fun fGiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addGiven(name, xmethod = TestXMethod.FOCUSED, test)

   suspend fun fgiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addGiven(name, xmethod = TestXMethod.FOCUSED, test)

   suspend fun xGiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addGiven(name, xmethod = TestXMethod.DISABLED, test)

   suspend fun xgiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addGiven(name, xmethod = TestXMethod.DISABLED, test)

   private suspend fun addGiven(
      name: String,
      xmethod: TestXMethod,
      test: suspend BehaviorSpecGivenContainerScope.() -> Unit,
   ) {
      registerTest(
         TestDefinitionBuilder.builder(givenName(name), TestType.Container)
            .withXmethod(xmethod)
            .build { BehaviorSpecGivenContainerScope(this).test() }
      )
   }

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
         TestDefinitionBuilder.builder(andName(name), TestType.Container)
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

   suspend fun `when`(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) = When(name, test)

   suspend fun When(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) {
      registerTest(
         TestDefinitionBuilder.builder(whenName(name), TestType.Container)
            .withXmethod(TestXMethod.NONE)
            .build { BehaviorSpecWhenContainerScope(this).test() }
      )
   }

   suspend fun xWhen(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) = xwhen(name, test)

   suspend fun xwhen(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) {
      registerTest(
         TestDefinitionBuilder.builder(whenName(name), TestType.Container)
            .withXmethod(TestXMethod.DISABLED)
            .build { BehaviorSpecWhenContainerScope(this).test() }
      )
   }

   fun `when`(name: String) = When(name)

   fun When(name: String) =
      ContainerWithConfigBuilder(
         name = whenName(name),
         context = this,
         xmethod = TestXMethod.NONE
      ) { BehaviorSpecWhenContainerScope(it) }

   fun xWhen(name: String) = xwhen(name)

   fun xwhen(name: String) =
      ContainerWithConfigBuilder(
         name = whenName(name),
         context = this,
         xmethod = TestXMethod.DISABLED
      ) { BehaviorSpecWhenContainerScope(it) }

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

   private fun givenName(name: String): TestName =
      TestNameBuilder.builder(name).withPrefix("Given: ").withDefaultAffixes().build()

   private fun whenName(name: String): TestName =
      TestNameBuilder.builder(name).withPrefix("When: ").withDefaultAffixes().build()

   private fun andName(name: String): TestName =
      TestNameBuilder.builder(name).withPrefix("And: ").withDefaultAffixes().build()

   private fun thenName(name: String): TestName =
      TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build()
}
