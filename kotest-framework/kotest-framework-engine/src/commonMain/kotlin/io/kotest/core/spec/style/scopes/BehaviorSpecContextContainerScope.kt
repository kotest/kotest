package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestScope

@Suppress("FunctionName")
@KotestTestScope
class BehaviorSpecContextContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope<BehaviorSpecContextContainerScope>(testScope) {

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

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the given [nameFn] function.
    */
   override suspend fun <T> withData(
      nameFn: (T) -> String,
      ts: Iterable<T>,
      test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
   ) {
      ts.forEach { t ->
         registerContainer(TestName("Context: ", nameFn(t), true), false, null) {
            BehaviorSpecContextContainerScope(this).test(t)
         }
      }
   }
}
