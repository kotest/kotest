package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.TestXMethod

interface WordSpecRootScope : RootScope {

   infix fun String.should(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, xmethod = TestXMethod.NONE, test = test)
   }

   infix fun String.fshould(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, xmethod = TestXMethod.FOCUSED, test = test)
   }

   infix fun String.xshould(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, xmethod = TestXMethod.DISABLED, test = test)
   }

   private fun should(name: String, xmethod: TestXMethod, test: suspend WordSpecShouldContainerScope.() -> Unit) {
      addContainer(
         testName = TestNameBuilder.builder(name).withSuffix(" should").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = null,
      ) { WordSpecShouldContainerScope(this).test() }
   }

   infix fun String.When(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xmethod = TestXMethod.NONE,
      test = init
   )

   infix fun String.fWhen(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xmethod = TestXMethod.FOCUSED,
      test = init
   )

   infix fun String.xWhen(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xmethod = TestXMethod.DISABLED,
      test = init
   )

   infix fun String.`when`(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xmethod = TestXMethod.NONE,
      test = init
   )

   infix fun String.fwhen(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xmethod = TestXMethod.FOCUSED,
      test = init
   )

   infix fun String.xwhen(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xmethod = TestXMethod.DISABLED,
      test = init
   )

   private fun `when`(
      name: String,
      xmethod: TestXMethod,
      test: suspend WordSpecWhenContainerScope.() -> Unit
   ) {
      addContainer(
         testName = TestNameBuilder.builder(name).withSuffix(" when").withDefaultAffixes().build(),
         xmethod = xmethod,
         config = null,
      ) { WordSpecWhenContainerScope(this).test() }
   }
}
