package io.kotest.core.spec.style.scopes

import io.kotest.core.execution.ExecutionContext
import io.kotest.core.plan.createTestName
import io.kotest.core.spec.KotestDsl
import io.kotest.core.spec.resolvedDefaultConfig
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createNestedTest
import kotlin.coroutines.CoroutineContext

@Suppress("FunctionName")
@KotestDsl
class WordSpecWhenContainerContext(
   val testContext: TestContext,
) : ContainerContext {

   override val testCase: TestCase = testContext.testCase
   override val coroutineContext: CoroutineContext = testContext.coroutineContext
   override val executionContext: ExecutionContext = testContext.executionContext
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
            name = createTestName("$name should"),
            xdisabled = xdisabled,
            config = testCase.spec.resolvedDefaultConfig(),
            type = TestType.Container,
            factoryId = testCase.factoryId,
            test = { WordSpecShouldContainerContext(this).test() }
         )
      )
   }
}
