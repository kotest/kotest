package io.kotest.datatest.tags
// tests in this package all have the same structure but different style (FunSpec, FreeSpec, BehaviorSpec ...) - keep it that way so they can all be tested at once
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.datatest.withContexts
import io.kotest.datatest.withShoulds
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe




class DataTestTagsShouldSpec : ShouldSpec({

   context("parent context") {
      context("child context") {
         withContexts("firstChildOfChildContext1", "firstChildOfChildContext2") { // kotest.data.nonJvm
            withShoulds("firstChildOfFirstChildOfChildContext1", "firstChildOfFirstChildOfChildContext2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
            withData("secondChildOfFirstChildOfChildContext1", "secondChildOfFirstChildOfChildContext2") { // kotest.data.nonJvm
               withShoulds("firstChildOfsecondChildOfFirstChildOfChildContext1", "firstChildOfsecondChildOfFirstChildOfChildContext2") { // kotest.data.nonJvm
                  1 + 1 shouldBe 2
               }
            }
         }
         withShoulds("secondChildOfChildContext1", "secondChildOfChildContext2") { // kotest.data.nonJvm
            1 + 1 shouldBe 2
         }
      }
      withShoulds("firstChildOfParentContext1", "firstChildOfParentContext2") { // kotest.data.nonJvm
         1 + 1 shouldBe 2
      }
   }


   withData("parent1", "parent2", "parent3") { // kotest.data.nonJvm
      withContexts("firstChild1", "firstChild2") { // kotest.data.nonJvm
         1 + 1 shouldBe 2
      }
      withContexts("secondChild1", "secondChild2") { // kotest.data.nonJvm
         withContexts("firstChildOfSecondChild1", "firstChildOfSecondChild2") { // kotest.data.nonJvm
            withShoulds("firstChildOfFirstChildOfSecondChild1", "firstChildOfFirstChildOfSecondChild2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
            withContexts("secondChildOfFirstChildOfSecondChild1", "secondChildOfFirstChildOfSecondChild2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
         }
         withShoulds("secondChildOfSecondChild1", "secondChildOfSecondChild2") { // kotest.data.nonJvm
            1 + 1 shouldBe 2
         }
         withData("thirdChildOfSecondChild1", "thirdChildOfSecondChild2") { // kotest.data.nonJvm
            withShoulds("firstAndOnlyChildOfThirdChildOfSecondChild1", "firstAndOnlyChildOfThirdChildOfSecondChild2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
         }
      }
      withShoulds("thirdChild1", "thirdChild2") { // kotest.data.nonJvm
         1 + 1 shouldBe 2
      }
   }
})
