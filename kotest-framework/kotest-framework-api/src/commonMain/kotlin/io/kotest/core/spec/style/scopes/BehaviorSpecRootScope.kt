package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName

@Deprecated("Renamed to BehaviorSpecRootScope. Deprecated since 5.0")
typealias BehaviorSpecRootContext = BehaviorSpecRootScope

/**
 * A context that allows tests to be registered using the syntax:
 *
 * given("some test")
 * xgiven("some disabled test")
 */
@Suppress("FunctionName")
interface BehaviorSpecRootScope : RootScope {

   /**
    * Adds a top level [BehaviorSpecGivenContainerScope] to this spec.
    */
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

   private fun addGiven(name: String, xdisabled: Boolean, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) {
      addContainer(
         TestName("Given: ", name, true),
         disabled = xdisabled,
         null
      ) { BehaviorSpecGivenContainerScope(this).test() }
   }
}
