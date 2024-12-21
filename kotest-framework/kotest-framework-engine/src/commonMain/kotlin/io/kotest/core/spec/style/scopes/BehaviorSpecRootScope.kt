package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.engine.stable.StableIdents

/**
 * A context that allows tests to be registered using the syntax:
 *
 * ```
 * given("some test")
 * xgiven("some disabled test")
 * ```
 */
interface BehaviorSpecRootScope : RootScope {

   /**
    * Adds a top level [BehaviorSpecGivenContainerScope] to this spec.
    */
   @Suppress("FunctionName")
   fun Given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) = addGiven(name, false, test)

   /**
    * Adds a top level [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) = addGiven(name, false, test)

   /**
    * Adds a top level disabled [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun xgiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) = addGiven(name, true, test)

   /**
    * Adds a top level disabled [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun xGiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) = addGiven(name, true, test)

   fun addGiven(name: String, xdisabled: Boolean, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) {
      addContainer(
         TestName("Given: ", name, true),
         disabled = xdisabled,
         null
      ) { BehaviorSpecGivenContainerScope(this).test() }
   }

   /**
    * Adds a top level [BehaviorSpecContextContainerScope] to this spec.
    */
   @Suppress("FunctionName")
   fun Context(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) = addContext(name, false, test)

   /**
    * Adds a top level [BehaviorSpecContextContainerScope] to this spec.
    */
   fun context(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) = addContext(name, false, test)

   /**
    * Adds a top level disabled [BehaviorSpecContextContainerScope] to this spec.
    */
   fun xcontext(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) = addContext(name, true, test)

   /**
    * Adds a top level disabled [BehaviorSpecContextContainerScope] to this spec.
    */
   fun xContext(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) = addContext(name, true, test)

   fun addContext(name: String, xdisabled: Boolean, test: suspend BehaviorSpecContextContainerScope.() -> Unit) {
      addContainer(
         TestName("Context: ", name, true),
         disabled = xdisabled,
         null
      ) { BehaviorSpecContextContainerScope(this).test() }
   }

   // data-test DSL follows

   /**
    * Registers tests at the root level for each element.
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   fun <T> withData(
      first: T,
      second: T, // we need two elements here so the compiler can disambiguate from the sequence version
      vararg rest: T,
      test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
   ) {
      withData(listOf(first, second) + rest, test)
   }

   fun <T> withData(
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
   fun <T> withData(ts: Sequence<T>, test: suspend BehaviorSpecContextContainerScope.(T) -> Unit) {
      withData(ts.toList(), test)
   }

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   fun <T> withData(nameFn: (T) -> String, ts: Sequence<T>, test: suspend BehaviorSpecContextContainerScope.(T) -> Unit) {
      withData(nameFn, ts.toList(), test)
   }

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the stable properties of the elements. See [StableIdents].
    */
   fun <T> withData(ts: Iterable<T>, test: suspend BehaviorSpecContextContainerScope.(T) -> Unit) {
      withData({ StableIdents.getStableIdentifier(it) }, ts, test)
   }

   /**
    * Registers tests at the root level for each element of [ts].
    *
    * The test name will be generated from the given [nameFn] function.
    */
   fun <T> withData(
      nameFn: (T) -> String,
      ts: Iterable<T>,
      test: suspend BehaviorSpecContextContainerScope.(T) -> Unit
   ) {
      ts.forEach { t ->
         addContainer(
            TestName("Context: ", nameFn(t), true),
            disabled = false,
            null
         ) { BehaviorSpecContextContainerScope(this).test(t) }
      }
   }
}
