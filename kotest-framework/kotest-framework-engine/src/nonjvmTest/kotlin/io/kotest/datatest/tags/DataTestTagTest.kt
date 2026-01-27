package io.kotest.datatest.tags

import io.kotest.assertions.assertSoftly
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.datatest.withTests
import io.kotest.engine.TestEngineLauncher
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class DataTestTagTest : FunSpec({

   withTests(
      nameFn = {"withXXX applies data test tags to generated tests for $it"},
      DataTestTagsFunSpec::class,
      DataTestTagsWordSpec::class,
      DataTestTagsShouldSpec::class,
      DataTestTagsFreeSpec::class,
      DataTestTagsFeatureSpec::class,
      DataTestTagsExpectSpec::class,
      DataTestTagsDescribeSpec::class,
      DataTestTagsBehaviorSpec::class,
   ){ testClass ->
      val capturedTests = mutableListOf<TestCase>()

      TestEngineLauncher()
         .withSpecRefs(SpecRef.Reference(testClass))
         .addExtension(object : BeforeTestListener {
            override suspend fun beforeTest(testCase: TestCase) {
               // skip `parent context` and `child context` as they are not data tests
               if(testCase.config?.tags?.isNotEmpty() == true){
                  capturedTests.add(testCase)
               }
            }
         })
         .execute()

      // Test count breakdown:
      // Inside context("parent context") / context("child context"):
      //   - 2 firstChildOfChildContext (intermediate) -> each contains:
      //       - 2 firstChildOfFirstChildOfChildContext (leaf)
      //       - 2 secondChildOfFirstChildOfChildContext (intermediate) -> each contains:
      //           - 2 firstChildOfsecondChildOfFirstChildOfChildContext (leaf)
      //   - 2 secondChildOfChildContext (leaf)
      // Inside context("parent context"):
      //   - 2 firstChildOfParentContext (leaf)
      // Total in context blocks: 2*(2 + 2*2) + 2 + 2 = 2*(2+4) + 4 = 12 + 4 = 16
      // Plus intermediate: 2 firstChildOfChildContext + 2*2 secondChildOfFirstChildOfChildContext = 2 + 4 = 6
      // Context block total: 16 + 6 = 22
      //
      // 3 parents (parent1, parent2, parent3) at root level
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
      // Total root level: 3 * 43 = 129
      //
      // Grand total: 22 + 129 = 151
      capturedTests shouldHaveSize 151

      capturedTests.forEach { testCase ->
         testCase.config shouldNotBe null
         val tagNames = testCase.config?.tags?.map { it.name } ?: emptyList()
         val testName = testCase.name.name
         assertSoftly {
            tagNames shouldHaveSize 2
            tagNames.first() shouldBe "kotest.data"
            // Each test should have a kotest.data.{lineNumber} tag based on its line number
            val expectedLineTag = when (testName) {
               // Tests inside context blocks (lines 16-30)
               in listOf("firstChildOfChildContext1", "firstChildOfChildContext2") -> "kotest.data.nonJvm"
               in listOf("firstChildOfFirstChildOfChildContext1", "firstChildOfFirstChildOfChildContext2") -> "kotest.data.nonJvm"
               in listOf("secondChildOfFirstChildOfChildContext1", "secondChildOfFirstChildOfChildContext2") -> "kotest.data.nonJvm"
               in listOf("firstChildOfsecondChildOfFirstChildOfChildContext1", "firstChildOfsecondChildOfFirstChildOfChildContext2") -> "kotest.data.nonJvm"
               in listOf("secondChildOfChildContext1", "secondChildOfChildContext2") -> "kotest.data.nonJvm"
               in listOf("firstChildOfParentContext1", "firstChildOfParentContext2") -> "kotest.data.nonJvm"
               // Tests at root level (lines 36-58)
               in listOf("parent1", "parent2", "parent3") -> "kotest.data.nonJvm"
               in listOf("firstChild1", "firstChild2") -> "kotest.data.nonJvm"
               in listOf("secondChild1", "secondChild2") -> "kotest.data.nonJvm"
               in listOf("firstChildOfSecondChild1", "firstChildOfSecondChild2") -> "kotest.data.nonJvm"
               in listOf("firstChildOfFirstChildOfSecondChild1", "firstChildOfFirstChildOfSecondChild2") -> "kotest.data.nonJvm"
               in listOf("secondChildOfFirstChildOfSecondChild1", "secondChildOfFirstChildOfSecondChild2") -> "kotest.data.nonJvm"
               in listOf("secondChildOfSecondChild1", "secondChildOfSecondChild2") -> "kotest.data.nonJvm"
               in listOf("thirdChildOfSecondChild1", "thirdChildOfSecondChild2") -> "kotest.data.nonJvm"
               in listOf("firstAndOnlyChildOfThirdChildOfSecondChild1", "firstAndOnlyChildOfThirdChildOfSecondChild2") -> "kotest.data.nonJvm"
               in listOf("thirdChild1", "thirdChild2") -> "kotest.data.nonJvm"
               else -> error("Unknown test name: $testName")
            }
            tagNames.last() shouldBe expectedLineTag
         }
      }
   }

   test("withXXX applies data test tags to generated tests for StringSpec") {
      val capturedTests = mutableListOf<TestCase>()

      TestEngineLauncher()
         .withSpecRefs(SpecRef.Reference(DataTestTagsStringSpec::class))
         .addExtension(object : BeforeTestListener {
            override suspend fun beforeTest(testCase: TestCase) {
               if(testCase.config?.tags?.isNotEmpty() == true){
                  capturedTests.add(testCase)
               }
            }
         })
         .execute()

      // StringSpec only supports root-level tests, so we have 4 data tests total
      capturedTests shouldHaveSize 4

      capturedTests.forEach { testCase ->
         testCase.config shouldNotBe null
         val tagNames = testCase.config?.tags?.map { it.name } ?: emptyList()
         val testName = testCase.name.name
         assertSoftly {
            tagNames shouldHaveSize 2
            tagNames.first() shouldBe "kotest.data"
            val expectedLineTag = when (testName) {
               in listOf("test1", "test2") -> "kotest.data.nonJvm"
               in listOf("test3", "test4") -> "kotest.data.nonJvm"
               else -> error("Unknown test name: $testName")
            }
            tagNames.last() shouldBe expectedLineTag
         }
      }
   }
})
