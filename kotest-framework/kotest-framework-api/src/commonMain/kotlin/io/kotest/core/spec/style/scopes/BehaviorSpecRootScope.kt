package io.kotest.core.spec.style.scopes

import io.kotest.core.test.createTestName

/**
 * A context that allows tests to be registered using the syntax:
 *
 * given("some test")
 * xgiven("some disabled test")
 */
@Suppress("FunctionName")
interface BehaviorSpecRootScope : RootScope {

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
      val testName = createTestName("Given: ", name, true)
      registration().addContainerTest(testName, xdisabled) {
         BehaviorSpecGivenContainerContext(this).test()
      }
   }
}
