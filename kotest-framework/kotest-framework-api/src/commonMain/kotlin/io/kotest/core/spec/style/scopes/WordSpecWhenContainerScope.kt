package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.TestScope

@Deprecated("Renamed to WordSpecWhenContainerScope. Deprecated since 5.0")
typealias WordSpecWhenScope = WordSpecWhenContainerScope

typealias WordSpecWhenContainerContext = WordSpecWhenContainerScope

@Suppress("FunctionName")
@KotestDsl
class WordSpecWhenContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope) {

   suspend infix fun String.Should(test: suspend WordSpecShouldContainerScope.() -> Unit) = addShould(this, test, false)
   suspend infix fun String.should(test: suspend WordSpecShouldContainerScope.() -> Unit) = addShould(this, test, false)
   suspend infix fun String.xshould(test: suspend WordSpecShouldContainerScope.() -> Unit) = addShould(this, test, true)

   private suspend fun addShould(name: String, test: suspend WordSpecShouldContainerScope.() -> Unit, xdisabled: Boolean) {
      registerContainer(TestName("$name should"), xdisabled, null) { WordSpecShouldContainerScope(this).test() }
   }
}
