package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName

@Deprecated("Renamed to BehaviorSpecRootContext. Deprecated since 4.5.")
typealias BehaviorSpecRootScope = BehaviorSpecRootContext

/**
 * A context that allows tests to be registered using the syntax:
 *
 * given("some test")
 * xgiven("some disabled test")
 */
@Suppress("FunctionName")
interface BehaviorSpecRootContext : RootContext {

   /**
    * Adds a top level [BehaviorSpecGivenContainerContext] to this spec.
    */
   fun Given(name: String, test: suspend BehaviorSpecGivenContainerContext.() -> Unit) = addGiven(name, false, test)

   /**
    * Adds a top level [BehaviorSpecGivenContainerContext] to this spec.
    */
   fun given(name: String, test: suspend BehaviorSpecGivenContainerContext.() -> Unit) = addGiven(name, false, test)

   /**
    * Adds a top level disabled [BehaviorSpecGivenContainerContext] to this spec.
    */
   fun xgiven(name: String, test: suspend BehaviorSpecGivenContainerContext.() -> Unit) = addGiven(name, true, test)

   /**
    * Adds a top level disabled [BehaviorSpecGivenContainerContext] to this spec.
    */
   fun xGiven(name: String, test: suspend BehaviorSpecGivenContainerContext.() -> Unit) = addGiven(name, true, test)

   private fun addGiven(name: String, xdisabled: Boolean, test: suspend BehaviorSpecGivenContainerContext.() -> Unit) {
      addContainer(TestName("Given: ", name, true), xdisabled, null) { BehaviorSpecGivenContainerContext(this).test() }
   }
}
