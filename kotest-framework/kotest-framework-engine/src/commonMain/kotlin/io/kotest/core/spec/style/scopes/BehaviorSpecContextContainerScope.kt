package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope

@Suppress("FunctionName")
@KotestTestScope
class BehaviorSpecContextContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope) {

   suspend fun Given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xmethod = TestXMethod.NONE, test)

   suspend fun given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      given(name, xmethod = TestXMethod.NONE, test)

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
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("Given: ").build(),
         xmethod = xmethod,
         config = null
      ) {
         BehaviorSpecGivenContainerScope(this).test()
      }
   }

   suspend fun Given(name: String) =
      addGiven(name, xmethod = TestXMethod.NONE)

   suspend fun given(name: String) =
      addGiven(name, xmethod = TestXMethod.NONE)

   suspend fun fgiven(name: String) =
      addGiven(name, xmethod = TestXMethod.FOCUSED)

   suspend fun fGiven(name: String) =
      addGiven(name, xmethod = TestXMethod.FOCUSED)

   suspend fun xgiven(name: String) =
      addGiven(name, xmethod = TestXMethod.DISABLED)

   suspend fun xGiven(name: String) =
      addGiven(name, xmethod = TestXMethod.DISABLED)

   private suspend fun addGiven(
      name: String,
      xmethod: TestXMethod
   ): ContainerWithConfigBuilder<BehaviorSpecGivenContainerScope> {
      return ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Given: ").withDefaultAffixes().build(),
         context = this,
         xmethod = xmethod
      ) { BehaviorSpecGivenContainerScope(it) }
   }

   internal suspend fun context(
      name: String,
      xmethod: TestXMethod,
      test: suspend BehaviorSpecContextContainerScope.() -> Unit,
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         xmethod = xmethod,
         config = null
      ) {
         BehaviorSpecContextContainerScope(this).test()
      }
   }

   @Suppress("FunctionName")
   suspend fun Context(name: String) =
      addContext(name = name, xmethod = TestXMethod.NONE)

   suspend fun context(name: String) =
      addContext(name = name, xmethod = TestXMethod.NONE)

   suspend fun xcontext(name: String) =
      addContext(name = name, xmethod = TestXMethod.DISABLED)

   suspend fun xContext(name: String) =
      addContext(name = name, xmethod = TestXMethod.DISABLED)

   private suspend fun addContext(
      name: String,
      xmethod: TestXMethod
   ): ContainerWithConfigBuilder<BehaviorSpecContextContainerScope> {
      return ContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").withDefaultAffixes().build(),
         context = this,
         xmethod = xmethod
      ) { BehaviorSpecContextContainerScope(it) }
   }
}
