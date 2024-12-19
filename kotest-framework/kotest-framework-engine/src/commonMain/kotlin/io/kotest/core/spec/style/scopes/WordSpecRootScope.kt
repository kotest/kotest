package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName

interface WordSpecRootScope : RootScope {

   infix fun String.should(test: suspend WordSpecShouldContainerScope.() -> Unit) = addShould(this, false, test)

   infix fun String.xshould(test: suspend WordSpecShouldContainerScope.() -> Unit) = addShould(this, true, test)

   private fun addShould(name: String, disabled: Boolean, test: suspend WordSpecShouldContainerScope.() -> Unit) {
      addContainer(TestName(null, name, " should", true), disabled, null) { WordSpecShouldContainerScope(this).test() }
   }

   @Suppress("FunctionName")
   infix fun String.When(init: suspend WordSpecWhenContainerScope.() -> Unit) = addWhen(this, init)
   infix fun String.`when`(init: suspend WordSpecWhenContainerScope.() -> Unit) = addWhen(this, init)

   private fun addWhen(name: String, test: suspend WordSpecWhenContainerScope.() -> Unit) {
      addContainer(TestName(null, name, " when", true), false, null) { WordSpecWhenContainerScope(this).test() }
   }
}
