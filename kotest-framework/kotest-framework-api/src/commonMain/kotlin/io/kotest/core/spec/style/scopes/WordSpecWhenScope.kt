package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName
import kotlin.coroutines.CoroutineContext

@Suppress("FunctionName")
@KotestDsl
class WordSpecWhenScope(
   override val description: Description,
   override val lifecycle: Lifecycle,
   override val testContext: TestContext,
   override val defaultConfig: TestCaseConfig,
   override val coroutineContext: CoroutineContext,
) : ContainerScope {

   suspend infix fun String.Should(test: suspend WordSpecShouldScope.() -> Unit) = addShould(this, test, false)
   suspend infix fun String.should(test: suspend WordSpecShouldScope.() -> Unit) = addShould(this, test, false)
   suspend infix fun String.xshould(test: suspend WordSpecShouldScope.() -> Unit) = addShould(this, test, true)

   private suspend fun addShould(name: String, test: suspend WordSpecShouldScope.() -> Unit, xdisabled: Boolean) {
      val testName = createTestName("$name should")
      addContainerTest(testName, xdisabled) {
         WordSpecShouldScope(
            this@WordSpecWhenScope.description.appendContainer(testName),
            this@WordSpecWhenScope.lifecycle,
            this,
            this@WordSpecWhenScope.defaultConfig,
            this@WordSpecWhenScope.coroutineContext,
         ).test()
      }
   }

}
