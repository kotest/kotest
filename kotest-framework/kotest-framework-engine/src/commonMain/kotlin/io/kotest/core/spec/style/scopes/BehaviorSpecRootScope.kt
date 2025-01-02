package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder

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
         testName = TestNameBuilder.builder(name).withPrefix("Given: ").withDefaultAffixes().build(),
         disabled = xdisabled,
         config = null
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
         testName = TestNameBuilder.builder(name).withPrefix("Context: ").withDefaultAffixes().build(),
         disabled = xdisabled,
         config = null
      ) { BehaviorSpecContextContainerScope(this).test() }
   }
}
