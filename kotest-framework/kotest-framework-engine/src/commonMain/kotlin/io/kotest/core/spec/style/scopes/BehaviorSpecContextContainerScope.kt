package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope

@Suppress("FunctionName")
@KotestTestScope
class BehaviorSpecContextContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope) {

   suspend fun Given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xmethod = TestXMethod.NONE, test)

   suspend fun given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xmethod = TestXMethod.NONE, test)

   suspend fun xgiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xmethod = TestXMethod.DISABLED, test)

   suspend fun xGiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xmethod = TestXMethod.DISABLED, test)

   internal suspend fun given(
      name: String,
      xmethod: TestXMethod,
      test: suspend BehaviorSpecGivenContainerScope.() -> Unit,
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("Given: ").build(),
         xmethod = xmethod,
         config = null
      ) {
         BehaviorSpecGivenContainerScope(this).test()
      }
   }

   internal suspend fun context(
      name: String,
      xmethod: TestXMethod,
      test: suspend BehaviorSpecContextContainerScope.() -> Unit,
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         xmethod = xmethod,
         config = null
      ) {
         BehaviorSpecContextContainerScope(this).test()
      }
   }
}
