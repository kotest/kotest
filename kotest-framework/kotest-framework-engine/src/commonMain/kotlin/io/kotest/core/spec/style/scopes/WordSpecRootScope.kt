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
         disabled = xdisabled,
         config = null,
      ) { WordSpecShouldContainerScope(this).test() }
   }

   @Suppress("FunctionName")
   infix fun String.When(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(this, false, init)
   infix fun String.xWhen(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(this, true, init)

   infix fun String.`when`(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(this, false, init)
   infix fun String.xwhen(init: suspend WordSpecWhenContainerScope.() -> Unit) = `when`(this, true, init)

   private fun `when`(name: String, xdisabled: Boolean, test: suspend WordSpecWhenContainerScope.() -> Unit) {
      addContainer(
         testName = TestNameBuilder.builder(name).withSuffix(" when").withDefaultAffixes().build(),
         disabled = xdisabled,
         config = null,
      ) { WordSpecWhenContainerScope(this).test() }
   }
}
