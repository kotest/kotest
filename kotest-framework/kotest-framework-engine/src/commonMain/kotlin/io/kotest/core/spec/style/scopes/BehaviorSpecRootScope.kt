package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.TestXMethod

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
   fun Given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) = addGiven(
      name = name,
      xmethod = TestXMethod.NONE,
      test = test
   )

   /**
    * Adds a top level [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) = addGiven(
      name = name,
      xmethod = TestXMethod.NONE,
      test = test
   )

   /**
    * Adds a top level disabled [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun xgiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) = addGiven(
      name = name,
      xmethod = TestXMethod.DISABLED,
      test = test
   )

   /**
    * Adds a top level disabled [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun xGiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) = addGiven(
      name = name,
      xmethod = TestXMethod.DISABLED,
      test = test
   )

   fun addGiven(name: String, xmethod: TestXMethod, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) {
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("Given: ").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = null
      ) { BehaviorSpecGivenContainerScope(this).test() }
   }

   /**
    * Adds a top level [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun given(name: String) = addGiven(name, false)

   /**
    * Adds a top level disabled [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun xgiven(name: String) = addGiven(name, true)

   /**
    * Adds a top level disabled [BehaviorSpecGivenContainerScope] to this spec.
    */
   @Suppress("FunctionName")
   fun Given(name: String) = addGiven(name, false)

   /**
    * Adds a top level disabled [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun xGiven(name: String) = addGiven(name, true)

   fun addGiven(name: String, xdisabled: Boolean): RootContainerWithConfigBuilder<BehaviorSpecGivenContainerScope> {
      return RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
         context = this@BehaviorSpecRootScope,
         xdisabled = false
      ) { BehaviorSpecGivenContainerScope(it) }
   }

   /**
    * Adds a top level [BehaviorSpecContextContainerScope] to this spec.
    */
   @Suppress("FunctionName")
   fun Context(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) =
      addContext(name = name, xmethod = TestXMethod.NONE, test = test)

   /**
    * Adds a top level [BehaviorSpecContextContainerScope] to this spec.
    */
   fun context(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) =
      addContext(name = name, xmethod = TestXMethod.NONE, test = test)

   /**
    * Adds a top level disabled [BehaviorSpecContextContainerScope] to this spec.
    */
   fun xcontext(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) =
      addContext(name = name, xmethod = TestXMethod.DISABLED, test = test)

   /**
    * Adds a top level disabled [BehaviorSpecContextContainerScope] to this spec.
    */
   fun xContext(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) =
      addContext(name = name, xmethod = TestXMethod.DISABLED, test = test)

   fun addContext(name: String, xmethod: TestXMethod, test: suspend BehaviorSpecContextContainerScope.() -> Unit) {
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("Context: ").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = null
      ) { BehaviorSpecContextContainerScope(this).test() }
   }
}
