package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder

interface WordSpecRootScope : RootScope {

   infix fun String.should(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, xdisabled = false, test = test)
   }

   infix fun String.xshould(test: suspend WordSpecShouldContainerScope.() -> Unit) {
      should(name = this, xdisabled = true, test = test)
   }

   private fun should(name: String, xdisabled: Boolean, test: suspend WordSpecShouldContainerScope.() -> Unit) {
      addContainer(
         testName = TestNameBuilder.builder(name).withSuffix(" should").withDefaultAffixes().build(),
         focused = false,
         disabled = xdisabled,
         config = null,
      ) { WordSpecShouldContainerScope(this).test() }
   }

   infix fun String.When(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xfocused = false,
      xdisabled = false,
      test = init
   )

   infix fun String.fWhen(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xfocused = true,
      xdisabled = false,
      test = init
   )

   infix fun String.xWhen(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xfocused = false,
      xdisabled = true,
      test = init
   )

   infix fun String.`when`(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xfocused = false,
      xdisabled = false,
      test = init
   )

   infix fun String.fwhen(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xfocused = true,
      xdisabled = false,
      test = init
   )

   infix fun String.xwhen(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(
      name = this,
      xfocused = false,
      xdisabled = true,
      test = init
   )

   private fun `when`(
      name: String,
      xfocused: Boolean,
      xdisabled: Boolean,
      test: suspend WordSpecWhenContainerScope.() -> Unit
   ) {
      addContainer(
         testName = TestNameBuilder.builder(name).withSuffix(" when").withDefaultAffixes().build(),
         focused = xfocused,
         disabled = xdisabled,
         config = null,
      ) { WordSpecWhenContainerScope(this).test() }
   }
}
