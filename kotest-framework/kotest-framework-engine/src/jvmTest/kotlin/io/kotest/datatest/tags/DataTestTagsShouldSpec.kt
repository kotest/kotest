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
         withContexts("firstChildOfChildContext1", "firstChildOfChildContext2") { // line 16 -> kotest.data.16
            withShoulds("firstChildOfFirstChildOfChildContext1", "firstChildOfFirstChildOfChildContext2") { // line 17 -> kotest.data.17
               1 + 1 shouldBe 2
            }
            withData("secondChildOfFirstChildOfChildContext1", "secondChildOfFirstChildOfChildContext2") { // line 20 -> kotest.data.20
               withShoulds("firstChildOfSecondChildOfFirstChildOfChildContext1", "firstChildOfSecondChildOfFirstChildOfChildContext2") { // line 21 -> kotest.data.21
                  1 + 1 shouldBe 2
               }
            }
         }
         withShoulds("secondChildOfChildContext1", "secondChildOfChildContext2") { // line 26 -> kotest.data.26
            1 + 1 shouldBe 2
         }
      }
      withShoulds("firstChildOfParentContext1", "firstChildOfParentContext2") { // line 30 -> kotest.data.30
         1 + 1 shouldBe 2
      }
   }


   withData("parent1", "parent2", "parent3") { // line 36 -> kotest.data.36
      withContexts("firstChild1", "firstChild2") { // line 37 -> kotest.data.37
         1 + 1 shouldBe 2
      }
      withContexts("secondChild1", "secondChild2") { // line 40 -> kotest.data.40
         withContexts("firstChildOfSecondChild1", "firstChildOfSecondChild2") { // line 41 -> kotest.data.41
            withShoulds("firstChildOfFirstChildOfSecondChild1", "firstChildOfFirstChildOfSecondChild2") { // line 42 -> kotest.data.42
               1 + 1 shouldBe 2
            }
            withContexts("secondChildOfFirstChildOfSecondChild1", "secondChildOfFirstChildOfSecondChild2") { // line 45 -> kotest.data.45
               1 + 1 shouldBe 2
            }
         }
         withShoulds("secondChildOfSecondChild1", "secondChildOfSecondChild2") { // line 49 -> kotest.data.49
            1 + 1 shouldBe 2
         }
         withData("thirdChildOfSecondChild1", "thirdChildOfSecondChild2") { // line 52 -> kotest.data.52
            withShoulds("firstAndOnlyChildOfThirdChildOfSecondChild1", "firstAndOnlyChildOfThirdChildOfSecondChild2") { // line 53 -> kotest.data.53
               1 + 1 shouldBe 2
            }
         }
      }
      withShoulds("thirdChild1", "thirdChild2") { // line 58 -> kotest.data.58
         1 + 1 shouldBe 2
      }
   }
})
