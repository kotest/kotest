package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestScope
import io.kotest.engine.stable.StableIdents

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


   // data-test DSL follows

   /**
    * Registers tests at the root level for each element.
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   suspend fun <T> withData(
      first: T,
      second: T, // we need two elements here so the compiler can disambiguate from the sequence version
      vararg rest: T,
      test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
   ) {
      withData(listOf(first, second) + rest, test)
   }

   suspend fun <T> withData(
      nameFn: (T) -> String,
      first: T,
      second: T,  // we need two elements here so the compiler can disambiguate from the sequence version
      vararg rest: T,
      test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
   ) = withData(nameFn, listOf(first, second) + rest, test)

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   suspend fun <T> withData(ts: Sequence<T>, test: suspend BehaviorSpecContextContainerScope.(T) -> Unit) {
      withData(ts.toList(), test)
   }

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   suspend fun <T> withData(
      nameFn: (T) -> String,
      ts: Sequence<T>,
      test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
   ) {
      withData(nameFn, ts.toList(), test)
   }

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   suspend fun <T> withData(ts: Iterable<T>, test: suspend BehaviorSpecContextContainerScope.(T) -> Unit) {
      withData({ StableIdents.getStableIdentifier(it) }, ts, test)
   }

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the given [nameFn] function.
    */
   suspend fun <T> withData(
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
