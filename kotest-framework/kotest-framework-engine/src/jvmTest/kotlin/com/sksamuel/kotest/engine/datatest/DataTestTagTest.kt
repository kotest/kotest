package com.sksamuel.kotest.engine.datatest

import io.kotest.assertions.assertSoftly
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.datatest.withData
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

      capturedTests shouldHaveSize 3

      capturedTests.forEach { testCase ->
         testCase.config shouldNotBe null
         val tagNames = testCase.config?.tags?.map { it.name } ?: emptyList()
         assertSoftly {
            tagNames shouldHaveSize 2
            tagNames.first() shouldBe "kotest.data"
            // 47 because withData is declared at line 47 in this file
            tagNames.last() shouldBe "kotest.data.47"

         }
      }
   }

})

private class DataTestTagTestSpec : FunSpec({
   withData(1, 2, 3) { value ->
      value shouldBe value
   }
})
