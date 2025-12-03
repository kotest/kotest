package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope

@Suppress("FunctionName")
@KotestTestScope
class WordSpecWhenContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope) {

   @Suppress("FunctionName")
   suspend infix fun String.When(init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.NONE, init)

   suspend infix fun String.fWhen(init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.FOCUSED, init)

   suspend infix fun String.xWhen(init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.DISABLED, init)

   suspend infix fun String.`when`(init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.NONE, init)

   suspend infix fun String.fwhen(init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.FOCUSED, init)

   suspend infix fun String.xwhen(init: suspend WordSpecWhenContainerScope.() -> Unit) =
      `when`(this, xmethod = TestXMethod.DISABLED, init)

   private suspend fun `when`(name: String, xmethod: TestXMethod, test: suspend WordSpecWhenContainerScope.() -> Unit) {
      registerContainer(
         name = TestNameBuilder.builder(name).withSuffix(" when").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = null,
      ) { WordSpecWhenContainerScope(this).test() }
   }

   suspend infix fun String.Should(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, test = test, xmethod = TestXMethod.NONE)
   }

   suspend infix fun String.should(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, test = test, xmethod = TestXMethod.NONE)
   }

   suspend infix fun String.fshould(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, test = test, xmethod = TestXMethod.FOCUSED)
   }

   suspend infix fun String.xshould(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, test = test, xmethod = TestXMethod.DISABLED)
   }

   private suspend fun should(
      name: String,
      test: suspend WordSpecShouldContainerScope.() -> Unit,
      xmethod: TestXMethod
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withSuffix(" should").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = null
      ) { WordSpecShouldContainerScope(this).test() }
   }
}

