package io.kotest.datatest.tags
// tests in this package all have the same structure but different style (FunSpec, FreeSpec, BehaviorSpec ...) - keep it that way so they can all be tested at once
import io.kotest.core.spec.style.WordSpec
import io.kotest.datatest.withWhens
import io.kotest.datatest.withShoulds
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe




class DataTestTagsWordSpec : WordSpec({

   "parent context" When {
      "child context" When {
         withWhens("firstChildOfChildContext1", "firstChildOfChildContext2") { // kotest.data.nonJvm
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
      withWhens("firstChildOfParentContext1", "firstChildOfParentContext2") { // kotest.data.nonJvm
         1 + 1 shouldBe 2
      }
   }

   // ancestorTestPath: null for all below as they are not wrapped in a container
   withData("parent1", "parent2", "parent3") { // kotest.data.nonJvm
      withWhens("firstChild1", "firstChild2") { // kotest.data.nonJvm
         1 + 1 shouldBe 2
      }
      withWhens("secondChild1", "secondChild2") { // kotest.data.nonJvm
         withWhens("firstChildOfSecondChild1", "firstChildOfSecondChild2") { // kotest.data.nonJvm
            withShoulds("firstChildOfFirstChildOfSecondChild1", "firstChildOfFirstChildOfSecondChild2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
            withShoulds("secondChildOfFirstChildOfSecondChild1", "secondChildOfFirstChildOfSecondChild2") { // kotest.data.nonJvm
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
