package com.sksamuel.kotest.engine.datatest

import io.kotest.assertions.assertSoftly
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.datatest.withContexts
import io.kotest.datatest.withData
import io.kotest.datatest.withTests
import io.kotest.engine.TestEngineLauncher
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.util.concurrent.CopyOnWriteArrayList

class DataTestTagTest : FunSpec({

   test("withData should apply data test tags to generated tests") {
      val capturedTests = CopyOnWriteArrayList<TestCase>()

      TestEngineLauncher()
         .withSpecRefs(SpecRef.Reference(DataTestTagTestSpec::class))
         .addExtension(object : BeforeTestListener {
            override suspend fun beforeTest(testCase: TestCase) {
               capturedTests.add(testCase)
            }
         })
         .execute()

      // Test count breakdown:
      // 3 parents (parent1, parent2, parent3)
      // Each parent contains:
      //   - 2 firstChild (leaf)
      //   - 2 secondChild (intermediate) -> each contains:
      //       - 2 firstChildOfSecondChild (intermediate) -> each contains:
      //           - 2 firstChildOfFirstChildOfSecondChild (leaf)
      //           - 2 secondChildOfFirstChildOfSecondChild (leaf)
      //       - 2 secondChildOfSecondChild (leaf)
      //       - 2 thirdChildOfSecondChild (intermediate) -> each contains:
      //           - 2 firstAndOnlyChildOfThirdChildOfSecondChild (leaf)
      //   - 2 thirdChild (leaf)
      // Per parent: 2 + 2*(2*(2+2) + 2 + 2*2) + 2 = 2 + 2*(8 + 2 + 4) + 2 = 32 leaf tests
      // Plus intermediate tests per parent: 2 secondChild + 2*2 firstChildOfSecondChild + 2*2 thirdChildOfSecondChild = 2 + 4 + 4 = 10
      // Total per parent: 32 + 10 = 42, but we also count parent itself = 43
      // Total: 3 * 43 = 129
      capturedTests shouldHaveSize 129

      capturedTests.forEach { testCase ->
         testCase.config shouldNotBe null
         val tagNames = testCase.config?.tags?.map { it.name } ?: emptyList()
         val testName = testCase.name.name
         assertSoftly {
            tagNames shouldHaveSize 2
            tagNames.first() shouldBe "kotest.data"
            // Each test should have a kotest.data.{lineNumber} tag based on its line number
            val expectedLineTag = when (testName) {
                in listOf("parent1", "parent2", "parent3") -> "kotest.data.78"
                in listOf("firstChild1", "firstChild2") -> "kotest.data.79"
                in listOf("secondChild1", "secondChild2") -> "kotest.data.82"
                in listOf("firstChildOfSecondChild1", "firstChildOfSecondChild2") -> "kotest.data.83"
                in listOf("firstChildOfFirstChildOfSecondChild1", "firstChildOfFirstChildOfSecondChild2") -> "kotest.data.84"
                in listOf("secondChildOfFirstChildOfSecondChild1", "secondChildOfFirstChildOfSecondChild2") -> "kotest.data.87"
                in listOf("secondChildOfSecondChild1", "secondChildOfSecondChild2") -> "kotest.data.91"
                in listOf("thirdChildOfSecondChild1", "thirdChildOfSecondChild2") -> "kotest.data.94"
                in listOf("firstAndOnlyChildOfThirdChildOfSecondChild1", "firstAndOnlyChildOfThirdChildOfSecondChild2") -> "kotest.data.95"
                in listOf("thirdChild1", "thirdChild2") -> "kotest.data.100"
                else -> error("Unknown test name: $testName")
            }
            tagNames.last() shouldBe expectedLineTag
         }
      }
   }

})

private class DataTestTagTestSpec : FunSpec({
   withData("parent1", "parent2", "parent3") { // line 78 -> kotest.data.78
      withTests("firstChild1", "firstChild2") { // line 79 -> kotest.data.79
         1 + 1 shouldBe 2
      }
      withData("secondChild1", "secondChild2") { // line 82 -> kotest.data.82
         withContexts("firstChildOfSecondChild1", "firstChildOfSecondChild2") { // line 83 -> kotest.data.83
            withTests("firstChildOfFirstChildOfSecondChild1", "firstChildOfFirstChildOfSecondChild2") { // line 84 -> kotest.data.84
               1 + 1 shouldBe 2
            }
            withTests("secondChildOfFirstChildOfSecondChild1", "secondChildOfFirstChildOfSecondChild2") { // line 87 -> kotest.data.87
               1 + 1 shouldBe 2
            }
         }
         withTests("secondChildOfSecondChild1", "secondChildOfSecondChild2") { // line 91 -> kotest.data.91
            1 + 1 shouldBe 2
         }
         withData("thirdChildOfSecondChild1", "thirdChildOfSecondChild2") { // line 94 -> kotest.data.94
            withTests("firstAndOnlyChildOfThirdChildOfSecondChild1", "firstAndOnlyChildOfThirdChildOfSecondChild2") { // line 95 -> kotest.data.95
               1 + 1 shouldBe 2
            }
         }
      }
      withTests("thirdChild1", "thirdChild2") { // line 100 -> kotest.data.100
         1 + 1 shouldBe 2
      }
   }
})
