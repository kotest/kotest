package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestScope

@Suppress("FunctionName")
@KotestTestScope
class WordSpecWhenContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope<WordSpecWhenContainerScope>(testScope) {

   suspend infix fun String.Should(test: suspend WordSpecShouldContainerScope.() -> Unit) = addShould(this, test, false)
   suspend infix fun String.should(test: suspend WordSpecShouldContainerScope.() -> Unit) = addShould(this, test, false)
   suspend infix fun String.xshould(test: suspend WordSpecShouldContainerScope.() -> Unit) = addShould(this, test, true)

   private suspend fun addShould(
      name: String,
      test: suspend WordSpecShouldContainerScope.() -> Unit,
      xdisabled: Boolean
   ) {
      registerContainer(
         name = TestName(null, name, " should", true),
         disabled = xdisabled,
         config = null
      ) { WordSpecShouldContainerScope(this).test() }
   }

   override suspend fun <T> withData(
      nameFn: (T) -> String,
      ts: Iterable<T>,
      test: suspend WordSpecWhenContainerScope.(T) -> Unit
   ) {
      ts.forEach { t ->
         registerContainer(name = TestName("context:", nameFn(t), true), disabled = false, config = null) {
            WordSpecWhenContainerScope(this).test(t)
         }
      }
   }
}
