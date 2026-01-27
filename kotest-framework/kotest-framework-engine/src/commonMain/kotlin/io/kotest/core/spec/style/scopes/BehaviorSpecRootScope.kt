package io.kotest.core.spec.style.scopes

import io.kotest.common.KotestInternal
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.config.TestConfig

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
    * Adds a test case with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   fun given(name: String, config: TestConfig, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) = addGiven(
      name = name,
      xmethod = TestXMethod.NONE,
      test = test,
      config = config
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

   fun addGiven(name: String, xmethod: TestXMethod, test: suspend BehaviorSpecGivenContainerScope.() -> Unit, config: TestConfig? = null) {
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("Given: ").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = config
      ) { BehaviorSpecGivenContainerScope(this).test() }
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
    * Adds a top level disabled [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun fgiven(name: String) =
      addGiven(name, TestXMethod.FOCUSED)

   /**
    * Adds a top level disabled [BehaviorSpecGivenContainerScope] to this spec.
    */
   fun fGiven(name: String) =
      addGiven(name, TestXMethod.FOCUSED)

   fun addGiven(name: String, xmethod: TestXMethod): RootContainerWithConfigBuilder<BehaviorSpecGivenContainerScope> {
      return RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Then: ").withDefaultAffixes().build(),
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
    * Adds a test case with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   fun context(name: String, config: TestConfig, test: suspend BehaviorSpecContextContainerScope.() -> Unit) =
      addContext(name = name, xmethod = TestXMethod.NONE, test = test, config = config)

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

   fun addContext(name: String, xmethod: TestXMethod, test: suspend BehaviorSpecContextContainerScope.() -> Unit, config: TestConfig? = null) {
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("Context: ").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = config
      ) { BehaviorSpecContextContainerScope(this).test() }
   }
}
