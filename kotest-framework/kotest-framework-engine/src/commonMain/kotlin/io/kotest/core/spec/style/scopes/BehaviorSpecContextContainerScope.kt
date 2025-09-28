package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestScope
import io.kotest.datatest.WithDataContainerRegistrar

@Suppress("FunctionName")
@KotestTestScope
class BehaviorSpecContextContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope), WithDataContainerRegistrar<BehaviorSpecContextContainerScope> {

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
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("Given: ").build(),
         disabled = xdisabled,
         config = null
      ) {
         BehaviorSpecGivenContainerScope(this).test()
      }
   }

   internal suspend fun context(
      name: String,
      xdisabled: Boolean,
      test: suspend BehaviorSpecContextContainerScope.() -> Unit,
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         disabled = xdisabled,
         config = null
      ) {
         BehaviorSpecContextContainerScope(this).test()
      }
   }

   override suspend fun registerWithDataTest(
      name: String,
      test: suspend BehaviorSpecContextContainerScope.() -> Unit
   ) {
      context(name, false) { test() }
   }
}
