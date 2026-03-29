package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType

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
      registerTest(
         TestDefinitionBuilder.builder(givenName(name), TestType.Container)
            .withXmethod(xmethod)
            .build { BehaviorSpecGivenContainerScope(this).test() }
      )
   }

   private fun givenName(name: String) =
      TestNameBuilder.builder(name).withPrefix("Given: ").withDefaultAffixes().build()

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
         name = givenName(name),
         scope = this,
         xmethod = xmethod
      ) { BehaviorSpecGivenContainerScope(it) }
   }

   internal suspend fun context(
      name: String,
      xmethod: TestXMethod,
      test: suspend BehaviorSpecContextContainerScope.() -> Unit,
   ) {
      registerTest(
         TestDefinitionBuilder.builder(contextName(name), TestType.Container)
            .withXmethod(xmethod)
            .build { BehaviorSpecContextContainerScope(this).test() }
      )
   }

   private fun contextName(name: String) =
      TestNameBuilder.builder(name).withPrefix("Context: ").withDefaultAffixes().build()

   @Suppress("FunctionName")
   fun Context(name: String) =
      addContext(name = name, xmethod = TestXMethod.NONE)

   fun context(name: String) =
      addContext(name = name, xmethod = TestXMethod.NONE)

   fun xcontext(name: String) =
      addContext(name = name, xmethod = TestXMethod.DISABLED)

   fun xContext(name: String) =
      addContext(name = name, xmethod = TestXMethod.DISABLED)

   private fun addContext(
      name: String,
      xmethod: TestXMethod
   ): ContainerWithConfigBuilder<BehaviorSpecContextContainerScope> {
      return ContainerWithConfigBuilder(
         name = contextName(name),
         scope = this,
         xmethod = xmethod
      ) { BehaviorSpecContextContainerScope(it) }
   }
}
