package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestScope

@Suppress("FunctionName")
@KotestTestScope
class BehaviorSpecContextContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope) {

   suspend fun Given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xdisabled = false, test)

   suspend fun given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xdisabled = false, test)

   suspend fun xgiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xdisabled = true, test)

   suspend fun xGiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xdisabled = true, test)

   internal suspend fun given(
      name: String,
      xdisabled: Boolean,
      test: suspend BehaviorSpecGivenContainerScope.() -> Unit,
   ) {
      registerContainer(name = TestName("Given: ", name, true), disabled = xdisabled, config = null) {
         BehaviorSpecGivenContainerScope(this).test()
      }
   }

   internal suspend fun context(
      name: String,
      xdisabled: Boolean,
      test: suspend BehaviorSpecContextContainerScope.() -> Unit,
   ) {
      registerContainer(name = TestName("Context: ", name, true), disabled = xdisabled, config = null) {
         BehaviorSpecContextContainerScope(this).test()
      }
   }
}
