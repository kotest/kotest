package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName

@Deprecated("Renamed to WordSpecRootContext. Deprecated since 4.5.")
typealias WordSpecRootScope = WordSpecRootContext

interface WordSpecRootContext : RootContext {

   infix fun String.should(test: suspend WordSpecShouldContainerContext.() -> Unit) = addShould(this, false, test)

   infix fun String.xshould(test: suspend WordSpecShouldContainerContext.() -> Unit) = addShould(this, true, test)

   private fun addShould(name: String, disabled: Boolean, test: suspend WordSpecShouldContainerContext.() -> Unit) {
      addContainer(TestName("$name should"), disabled, null) { WordSpecShouldContainerContext(this).test() }
   }

   @Suppress("FunctionName")
   infix fun String.When(init: suspend WordSpecWhenContainerContext.() -> Unit) = addWhen(this, init)
   infix fun String.`when`(init: suspend WordSpecWhenContainerContext.() -> Unit) = addWhen(this, init)

   private fun addWhen(name: String, test: suspend WordSpecWhenContainerContext.() -> Unit) {
      addContainer(TestName("$name when"), false, null) { WordSpecWhenContainerContext(this).test() }
   }
}
