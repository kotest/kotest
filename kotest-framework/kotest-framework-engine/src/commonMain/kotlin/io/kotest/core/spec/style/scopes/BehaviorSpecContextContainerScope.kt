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

   fun Given(name: String) =
      addGiven(name, xmethod = TestXMethod.NONE)

   fun given(name: String) =
      addGiven(name, xmethod = TestXMethod.NONE)

   fun xgiven(name: String) =
      addGiven(name, xmethod = TestXMethod.DISABLED)

   fun xGiven(name: String) =
      addGiven(name, xmethod = TestXMethod.DISABLED)

   private fun addGiven(
      name: String,
      xmethod: TestXMethod
   ): ContainerWithConfigBuilder<BehaviorSpecGivenContainerScope> {
      return ContainerWithConfigBuilder(
         name = givenName(name),
         context = this,
         xmethod = xmethod
      ) { BehaviorSpecGivenContainerScope(it) }
   }

   @Suppress("FunctionName")
   suspend fun Context(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) =
      addContext(name, xmethod = TestXMethod.NONE, test)

   suspend fun context(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) =
      addContext(name, xmethod = TestXMethod.NONE, test)

   suspend fun xContext(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) =
      addContext(name, xmethod = TestXMethod.DISABLED, test)

   suspend fun xcontext(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) =
      addContext(name, xmethod = TestXMethod.DISABLED, test)

   private suspend fun addContext(
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

   @Suppress("FunctionName")
   fun Context(name: String) =
      addContext(name = name, xmethod = TestXMethod.NONE)

   fun context(name: String) =
      addContext(name = name, xmethod = TestXMethod.NONE)

   fun xcontext(name: String) =
      addContext(name = name, xmethod = TestXMethod.DISABLED)

   fun xContext(name: String) =
      addContext(name = name, xmethod = TestXMethod.DISABLED)

   private fun givenName(name: String) =
      TestNameBuilder.builder(name).withPrefix("Given: ").withDefaultAffixes().build()

   private fun contextName(name: String) =
      TestNameBuilder.builder(name).withPrefix("Context: ").withDefaultAffixes().build()

   private fun addContext(
      name: String,
      xmethod: TestXMethod
   ): ContainerWithConfigBuilder<BehaviorSpecContextContainerScope> {
      return ContainerWithConfigBuilder(
         name = contextName(name),
         context = this,
         xmethod = xmethod
      ) { BehaviorSpecContextContainerScope(it) }
   }
}
