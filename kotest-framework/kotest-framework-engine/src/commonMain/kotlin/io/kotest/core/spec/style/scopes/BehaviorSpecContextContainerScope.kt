package io.kotest.core.spec.style.scopes

import io.kotest.common.KotestInternal
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.TestConfig

@Suppress("FunctionName")
@KotestTestScope
class BehaviorSpecContextContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope) {

   suspend fun Given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xmethod = TestXMethod.NONE, test)

   suspend fun given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xmethod = TestXMethod.NONE, test)

   /**
    * Adds a test case with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   suspend fun given(name: String, config: TestConfig, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xmethod = TestXMethod.NONE, test, config)

   suspend fun fgiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xmethod = TestXMethod.FOCUSED, test)

   suspend fun fGiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xmethod = TestXMethod.FOCUSED, test)

   suspend fun xgiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xmethod = TestXMethod.DISABLED, test)

   suspend fun xGiven(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xmethod = TestXMethod.DISABLED, test)

   internal suspend fun given(
      name: String,
      xmethod: TestXMethod,
      test: suspend BehaviorSpecGivenContainerScope.() -> Unit,
      config: TestConfig? = null
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("Given: ").build(),
         xmethod = xmethod,
         config = config
      ) {
         BehaviorSpecGivenContainerScope(this).test()
      }
   }

   internal suspend fun context(
      name: String,
      xmethod: TestXMethod,
      test: suspend BehaviorSpecContextContainerScope.() -> Unit,
      config: TestConfig? = null
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         xmethod = xmethod,
         config = config
      ) {
         BehaviorSpecContextContainerScope(this).test()
      }
   }
}
