package com.sksamuel.kotest.specs.funspec

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withContexts
import io.kotest.datatest.withData
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe

class DataTestSpecExample : FunSpec({
   withData("parent1", "parent2", "parent3") {
      withTests("firstChild1", "firstChild2") {
         1 + 1 shouldBe 2
      }
      withData("secondChild1", "secondChild2") {
         withContexts("firstChildOfSecondChild1", "firstChildOfSecondChild2") {
            withTests("firstChildOfFirstChildOfSecondChild1", "firstChildOfFirstChildOfSecondChild2") {
               1 + 1 shouldBe 2
            }
            withTests("secondChildOfFirstChildOfSecondChild1", "secondChildOfFirstChildOfSecondChild2") {
               1 + 1 shouldBe 2
            }
         }
         withTests("secondChildOfSecondChild1", "secondChildOfSecondChild2") {
            1 + 1 shouldBe 2
         }
         withData("thirdChildOfSecondChild1", "thirdChildOfSecondChild2") {
            withTests("firstAndOnlyChildOfThirdChildOfSecondChild1", "firstAndOnlyChildOfThirdChildOfSecondChild2") {
               1 + 1 shouldBe 2
            }
         }
      }
      withTests("thirdChild1", "thirdChild2") {
         1 + 1 shouldBe 2
      }
   }
})
