package io.kotest.datatest.tags
// tests in this package all have the same structure but different style (FunSpec, FreeSpec, BehaviorSpec ...) - keep it that way so they can all be tested at once
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.datatest.withFeatures
import io.kotest.datatest.withScenarios
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe




class DataTestTagsFeatureSpec : FeatureSpec({

   feature("parent context") {
      feature("child context") {
         withFeatures("firstChildOfChildContext1", "firstChildOfChildContext2") { // kotest.data.nonJvm
            withFeatures("firstChildOfFirstChildOfChildContext1", "firstChildOfFirstChildOfChildContext2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
            withFeatures("secondChildOfFirstChildOfChildContext1", "secondChildOfFirstChildOfChildContext2") { // kotest.data.nonJvm
               withFeatures("firstChildOfSecondChildOfFirstChildOfChildContext1", "firstChildOfSecondChildOfFirstChildOfChildContext2") { // kotest.data.nonJvm
                  1 + 1 shouldBe 2
               }
            }
         }
         withScenarios("secondChildOfChildContext1", "secondChildOfChildContext2") { // kotest.data.nonJvm
            1 + 1 shouldBe 2
         }
      }
      withScenarios("firstChildOfParentContext1", "firstChildOfParentContext2") { // kotest.data.nonJvm
         1 + 1 shouldBe 2
      }
   }


   withData("parent1", "parent2", "parent3") { // kotest.data.nonJvm
      withScenarios("firstChild1", "firstChild2") { // kotest.data.nonJvm
         1 + 1 shouldBe 2
      }
      withFeatures("secondChild1", "secondChild2") { // kotest.data.nonJvm
         withFeatures("firstChildOfSecondChild1", "firstChildOfSecondChild2") { // kotest.data.nonJvm
            withScenarios("firstChildOfFirstChildOfSecondChild1", "firstChildOfFirstChildOfSecondChild2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
            withData("secondChildOfFirstChildOfSecondChild1", "secondChildOfFirstChildOfSecondChild2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
         }
         withScenarios("secondChildOfSecondChild1", "secondChildOfSecondChild2") { // kotest.data.nonJvm
            1 + 1 shouldBe 2
         }
         withData("thirdChildOfSecondChild1", "thirdChildOfSecondChild2") { // kotest.data.nonJvm
            withScenarios("firstAndOnlyChildOfThirdChildOfSecondChild1", "firstAndOnlyChildOfThirdChildOfSecondChild2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
         }
      }
      withScenarios("thirdChild1", "thirdChild2") { // kotest.data.nonJvm
         1 + 1 shouldBe 2
      }
   }
})
