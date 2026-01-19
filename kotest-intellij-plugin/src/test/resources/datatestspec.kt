package com.sksamuel.kotest.specs.funspec

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withContexts
import io.kotest.datatest.withData
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe

class DataTestSpecExample : FunSpec({

    context("parent context") {
        context("child context") {
            withData("firstChildOfChildContext1", "firstChildOfChildContext2") { // tags: "(kotest.data.13) | !kotest.data" - ancestorTestPath: "parent context -- child context"
                1 + 1 shouldBe 2
            }
            withTests("secondChildOfChildContext1", "secondChildOfChildContext2") { // tags: "(kotest.data.16) | !kotest.data" - ancestorTestPath: "parent context -- child context"
                1 + 1 shouldBe 2
            }
        }
        withContexts("firstChildOfParentContext1", "firstChildOfParentContext2") { // tags: "(kotest.data.20) | !kotest.data" - ancestorTestPath: "parent context"
            1 + 1 shouldBe 2
        }
    }

   // ancestorTestPath: null for all below as they are not wrapped in a container
   withData("parent1", "parent2", "parent3") { // tag: "kotest.data.26"
      withTests("firstChild1", "firstChild2") { // tag: "kotest.data.26 & !kotest.data.30 & !kotest.data.48"
         1 + 1 shouldBe 2
      }
      withData("secondChild1", "secondChild2") { // tag: "kotest.data.26 & !kotest.data.27 & !kotest.data.48"
         withContexts("firstChildOfSecondChild1", "firstChildOfSecondChild2") { // tag: "kotest.data.26 & !kotest.data.27 & !kotest.data.48 & !kotest.data.39 & !kotest.data.42"
            withTests("firstChildOfFirstChildOfSecondChild1", "firstChildOfFirstChildOfSecondChild2") { // tags: "kotest.data.26 & !kotest.data.27 & !kotest.data.48 & !kotest.data.39 & !kotest.data.42 & !kotest.data.34 & !kotest.data.37"
               1 + 1 shouldBe 2
            }
            withTests("secondChildOfFirstChildOfSecondChild1", "secondChildOfFirstChildOfSecondChild2") { // tags: "kotest.data.26 & !kotest.data.27 & !kotest.data.48 & !kotest.data.39 & !kotest.data.42 & !kotest.data.34 & !kotest.data.37"
               1 + 1 shouldBe 2
            }
         }
         withTests("secondChildOfSecondChild1", "secondChildOfSecondChild2") { // tags: "kotest.data.26 & !kotest.data.27 & !kotest.data.48 & !kotest.data.31 & !kotest.data.42"
            1 + 1 shouldBe 2
         }
         withData("thirdChildOfSecondChild1", "thirdChildOfSecondChild2") { // tags: "kotest.data.26 & !kotest.data.27 & !kotest.data.48 & !kotest.data.31 & !kotest.data.39"
            withTests("firstAndOnlyChildOfThirdChildOfSecondChild1", "firstAndOnlyChildOfThirdChildOfSecondChild2") { // tags: "kotest.data.26 & !kotest.data.27 & !kotest.data.48 & !kotest.data.31 & !kotest.data.39" - same as parent as no siblings
               1 + 1 shouldBe 2
            }
         }
      }
      withTests("thirdChild1", "thirdChild2") { // tags: "kotest.data.26 & !kotest.data.27 & !kotest.data.30"
         1 + 1 shouldBe 2
      }
   }
})
