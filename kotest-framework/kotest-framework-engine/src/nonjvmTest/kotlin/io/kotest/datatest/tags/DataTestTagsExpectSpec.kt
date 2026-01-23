package io.kotest.datatest.tags
// tests in this package all have the same structure but different style (FunSpec, FreeSpec, BehaviorSpec ...) - keep it that way so they can all be tested at once
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.datatest.withContexts
import io.kotest.datatest.withExpects
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe




class DataTestTagsExpectSpec : ExpectSpec({

   context("parent context") {
      context("child context") {
         withContexts("firstChildOfChildContext1", "firstChildOfChildContext2") { // kotest.data.nonJvm
            withExpects("firstChildOfFirstChildOfChildContext1", "firstChildOfFirstChildOfChildContext2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
            withContexts("secondChildOfFirstChildOfChildContext1", "secondChildOfFirstChildOfChildContext2") { // kotest.data.nonJvm
               withExpects("firstChildOfsecondChildOfFirstChildOfChildContext1", "firstChildOfsecondChildOfFirstChildOfChildContext2") { // kotest.data.nonJvm
                  1 + 1 shouldBe 2
               }
            }
         }
         withContexts("secondChildOfChildContext1", "secondChildOfChildContext2") { // kotest.data.nonJvm
            1 + 1 shouldBe 2
         }
      }
      withContexts("firstChildOfParentContext1", "firstChildOfParentContext2") { // kotest.data.nonJvm
         1 + 1 shouldBe 2
      }
   }

   // ancestorTestPath: null for all below as they are not wrapped in a container
   withData("parent1", "parent2", "parent3") { // kotest.data.nonJvm
      withContexts("firstChild1", "firstChild2") { // kotest.data.nonJvm
         1 + 1 shouldBe 2
      }
      withContexts("secondChild1", "secondChild2") { // kotest.data.nonJvm
         withContexts("firstChildOfSecondChild1", "firstChildOfSecondChild2") { // kotest.data.nonJvm
            withExpects("firstChildOfFirstChildOfSecondChild1", "firstChildOfFirstChildOfSecondChild2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
            withData("secondChildOfFirstChildOfSecondChild1", "secondChildOfFirstChildOfSecondChild2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
         }
         withContexts("secondChildOfSecondChild1", "secondChildOfSecondChild2") { // kotest.data.nonJvm
            1 + 1 shouldBe 2
         }
         withData("thirdChildOfSecondChild1", "thirdChildOfSecondChild2") { // kotest.data.nonJvm
            withContexts("firstAndOnlyChildOfThirdChildOfSecondChild1", "firstAndOnlyChildOfThirdChildOfSecondChild2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
         }
      }
      withContexts("thirdChild1", "thirdChild2") { // kotest.data.nonJvm
         1 + 1 shouldBe 2
      }
   }
})
