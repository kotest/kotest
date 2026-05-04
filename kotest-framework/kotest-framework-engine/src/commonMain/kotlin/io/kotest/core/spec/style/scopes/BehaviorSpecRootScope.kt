package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestType

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

   /**
    * Adds a top level focused [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun fgiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) = addGiven(
      name = name,
      xmethod = TestXMethod.FOCUSED,
      test = test
   )

   /**
    * Adds a top level focused [BehaviorSpecGivenContainerScope] to this spec.
    */
   @Suppress("FunctionName")
   fun fGiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) = addGiven(
      name = name,
      xmethod = TestXMethod.FOCUSED,
      test = test
   )

   fun addGiven(name: String, xmethod: TestXMethod, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(givenName(name), TestType.Container)
            .withXmethod(xmethod)
            .build { BehaviorSpecGivenContainerScope(this).test() }
      )
   }

   /**
    * Adds a top level [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun given(name: String) =
      addGiven(name, TestXMethod.NONE)

   /**
    * Adds a top level disabled [BehaviorSpecGivenContainerScope] to this spec.
    */
   @Suppress("FunctionName")
   fun Given(name: String) =
      addGiven(name, TestXMethod.NONE)

   /**
    * Adds a top level disabled [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun xgiven(name: String) =
      addGiven(name, TestXMethod.DISABLED)

   /**
    * Adds a top level disabled [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun xGiven(name: String) =
      addGiven(name, TestXMethod.DISABLED)

   /**
    * Adds a top level focused [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun fgiven(name: String) =
      addGiven(name, TestXMethod.FOCUSED)

   /**
    * Adds a top level focused [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun fGiven(name: String) =
      addGiven(name, TestXMethod.FOCUSED)

   private fun addGiven(
      name: String,
      xmethod: TestXMethod
   ): RootContainerWithConfigBuilder<BehaviorSpecGivenContainerScope> {
      return RootContainerWithConfigBuilder(
         name = givenName(name),
         context = this@BehaviorSpecRootScope,
         xmethod = xmethod,
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

   /**
    * Adds a top level focused [BehaviorSpecContextContainerScope] to this spec.
    */
   fun fcontext(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) =
      addContext(name = name, xmethod = TestXMethod.FOCUSED, test = test)

   /**
    * Adds a top level focused [BehaviorSpecContextContainerScope] to this spec.
    */
   @Suppress("FunctionName")
   fun fContext(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) =
      addContext(name = name, xmethod = TestXMethod.FOCUSED, test = test)

   fun addContext(name: String, xmethod: TestXMethod, test: suspend BehaviorSpecContextContainerScope.() -> Unit) {
      add(
         TestDefinitionBuilder.builder(contextName(name), TestType.Container)
            .withXmethod(xmethod)
            .build { BehaviorSpecContextContainerScope(this).test() }
      )
   }

   /**
    * Adds a top level [BehaviorSpecContextContainerScope] to this spec.
    */
   @Suppress("FunctionName")
   fun Context(name: String) =
      addContext(name = name, xmethod = TestXMethod.NONE)

   /**
    * Adds a top level [BehaviorSpecContextContainerScope] to this spec.
    */
   fun context(name: String) = addContext(name = name, xmethod = TestXMethod.NONE)

   /**
    * Adds a top level disabled [BehaviorSpecContextContainerScope] to this spec.
    */
   fun xcontext(name: String) = addContext(name = name, xmethod = TestXMethod.DISABLED)

   /**
    * Adds a top level disabled [BehaviorSpecContextContainerScope] to this spec.
    */
   fun xContext(name: String) = addContext(name = name, xmethod = TestXMethod.DISABLED)

   /**
    * Adds a top level focused [BehaviorSpecContextContainerScope] to this spec.
    */
   fun fcontext(name: String) =
      addContext(name = name, xmethod = TestXMethod.FOCUSED)

   /**
    * Adds a top level focused [BehaviorSpecContextContainerScope] to this spec.
    */
   @Suppress("FunctionName")
   fun fContext(name: String) =
      addContext(name = name, xmethod = TestXMethod.FOCUSED)

   private fun addContext(
      name: String,
      xmethod: TestXMethod
   ): RootContainerWithConfigBuilder<BehaviorSpecContextContainerScope> {
      return RootContainerWithConfigBuilder(
         name = contextName(name),
         context = this@BehaviorSpecRootScope,
         xmethod = xmethod,
      ) { BehaviorSpecContextContainerScope(it) }
   }

   private fun contextName(name: String): TestName =
      TestNameBuilder.builder(name).withPrefix("Context: ").withDefaultAffixes().build()

   private fun givenName(name: String) =
      TestNameBuilder.builder(name).withPrefix("Given: ").withDefaultAffixes().build()
}
