package io.kotest.core.spec.style.scopes

import io.kotest.core.test.DescriptionName
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
    * Adds a top level [GivenScope] to this spec.
    */
   fun Given(name: String, test: suspend GivenScope.() -> Unit) = addGiven(name, false, test)

   /**
    * Adds a top level [GivenScope] to this spec.
    */
   fun given(name: String, test: suspend GivenScope.() -> Unit) = addGiven(name, false, test)

   /**
    * Adds a top level disabled [GivenScope] to this spec.
    */
   fun xgiven(name: String, test: suspend GivenScope.() -> Unit) = addGiven(name, true, test)

   /**
    * Adds a top level disabled [GivenScope] to this spec.
    */
   fun xGiven(name: String, test: suspend GivenScope.() -> Unit) = addGiven(name, true, test)

   private fun addGiven(name: String, xdisabled: Boolean, test: suspend GivenScope.() -> Unit) {
      val testName = createTestName("Given: ", name, true)
      registration().addContainerTest(testName, xdisabled) {
         GivenScope(
            description().appendContainer(testName),
            lifecycle(),
            this,
            defaultConfig(),
            this.coroutineContext,
         ).test()
      }
   }
}
