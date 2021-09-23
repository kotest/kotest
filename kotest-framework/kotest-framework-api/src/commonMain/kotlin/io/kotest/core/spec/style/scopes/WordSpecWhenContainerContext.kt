package io.kotest.core.spec.style.scopes

import io.kotest.core.descriptors.append
import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestDsl
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest

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
      registerTestCase(
         createNestedTest(
            descriptor = testCase.descriptor.append(name),
            name = TestName("$name should"),
            xdisabled = xdisabled,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            factoryId = testCase.factoryId,
            test = {
               val incomplete = IncompleteContainerContext(this)
               WordSpecShouldContainerContext(incomplete).test()
               if (!incomplete.registered) throw IncompleteContainerException(name)
            }
         )
      )
   }
}
