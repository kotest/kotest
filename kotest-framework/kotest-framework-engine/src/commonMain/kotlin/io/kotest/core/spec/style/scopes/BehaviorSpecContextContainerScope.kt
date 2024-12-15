package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestScope

@Suppress("FunctionName")
@KotestTestScope
class BehaviorSpecContextContainerScope(
   val testScope: TestScope
) : AbstractContainerScope(testScope) {

   suspend fun Given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addGiven(name, test, xdisabled = false)

   suspend fun given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addGiven(name, test, xdisabled = false)

   suspend fun xgiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addGiven(name, test, xdisabled = true)

   suspend fun xGiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      addGiven(name, test, xdisabled = true)

   private suspend fun addGiven(
      name: String,
      test: suspend BehaviorSpecGivenContainerScope.() -> Unit,
      xdisabled: Boolean
   ) {
      registerContainer(TestName("Given: ", name, true), xdisabled, null) {
         BehaviorSpecGivenContainerScope(this).test()
      }
   }

}
