package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType

@Deprecated("Renamed to WordSpecWhenContainerContext. Deprecated since 4.5.")
typealias WordSpecWhenScope = WordSpecWhenContainerContext

@Suppress("FunctionName")
@KotestDsl
class WordSpecWhenContainerContext(
   val testContext: TestContext,
) : AbstractContainerContext(testContext) {

   override suspend fun registerTestCase(nested: NestedTest) = testContext.registerTestCase(nested)

   override suspend fun addTest(name: String, type: TestType, test: suspend TestContext.() -> Unit) {
      when (type) {
         TestType.Container -> addShould(name, { WordSpecShouldContainerContext(this).test() }, false)
         TestType.Test -> error("Cannot add a test case here")
      }
   }

   suspend infix fun String.Should(test: suspend WordSpecShouldContainerContext.() -> Unit) = addShould(this, test, false)
   suspend infix fun String.should(test: suspend WordSpecShouldContainerContext.() -> Unit) = addShould(this, test, false)
   suspend infix fun String.xshould(test: suspend WordSpecShouldContainerContext.() -> Unit) = addShould(this, test, true)

   private suspend fun addShould(name: String, test: suspend WordSpecShouldContainerContext.() -> Unit, xdisabled: Boolean) {
      registerContainer(TestName("$name should"), xdisabled, null) { WordSpecShouldContainerContext(this).test() }
   }
}
