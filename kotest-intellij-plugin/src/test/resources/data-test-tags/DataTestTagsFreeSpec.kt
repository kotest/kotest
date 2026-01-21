package kotest.datatesttags
// tests in this package all have the same structure but different style (FunSpec, FreeSpec, BehaviorSpec) - keep it that way so they can all be tested at once
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withContexts
import io.kotest.datatest.withData
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe




class DataTestTagsFreeSpec : FreeSpec({

   "parent context" - {
      "child context" - {
         withData("firstChildOfChildContext1", "firstChildOfChildContext2") { // tags: "(kotest.data.16) | !kotest.data" - ancestorTestPath: "parent context -- child context"
            withTests("firstChildOfFirstChildOfChildContext1", "firstChildOfFirstChildOfChildContext2") { // tags: "(kotest.data.16 & !kotest.data.20) | !kotest.data" - ancestorTestPath: "parent context -- child context"
               1 + 1 shouldBe 2
            }
            withData("secondChildOfFirstChildOfChildContext1", "secondChildOfFirstChildOfChildContext2") { // tags: "(kotest.data.16 & !kotest.data.17) | !kotest.data" - ancestorTestPath: "parent context -- child context"
               withTests("firstChildOfsecondChildOfFirstChildOfChildContext1", "firstChildOfsecondChildOfFirstChildOfChildContext2") { // tags: "(kotest.data.16 & !kotest.data.17) | !kotest.data" - ancestorTestPath: "parent context -- child context" (same as parent, no siblings)
                  1 + 1 shouldBe 2
               }
            }
         }
         withTests("secondChildOfChildContext1", "secondChildOfChildContext2") { // tags: "(kotest.data.26) | !kotest.data" - ancestorTestPath: "parent context -- child context"
            1 + 1 shouldBe 2
         }
      }
      withContexts("firstChildOfParentContext1", "firstChildOfParentContext2") { // tags: "(kotest.data.30) | !kotest.data" - ancestorTestPath: "parent context"
         1 + 1 shouldBe 2
      }
   }

   // ancestorTestPath: null for all below as they are not wrapped in a container
   withData("parent1", "parent2", "parent3") { // tag: "kotest.data.36"
      withTests("firstChild1", "firstChild2") { // tag: "kotest.data.36 & !kotest.data.40 & !kotest.data.58"
         1 + 1 shouldBe 2
      }
      withData("secondChild1", "secondChild2") { // tag: "kotest.data.36 & !kotest.data.37 & !kotest.data.58"
         withContexts("firstChildOfSecondChild1", "firstChildOfSecondChild2") { // tag: "kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.49 & !kotest.data.52"
            withTests("firstChildOfFirstChildOfSecondChild1", "firstChildOfFirstChildOfSecondChild2") { // tags: "kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.49 & !kotest.data.52 & !kotest.data.45"
               1 + 1 shouldBe 2
            }
            withTests("secondChildOfFirstChildOfSecondChild1", "secondChildOfFirstChildOfSecondChild2") { // tags: "kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.49 & !kotest.data.52 & !kotest.data.42"
               1 + 1 shouldBe 2
            }
         }
         withTests("secondChildOfSecondChild1", "secondChildOfSecondChild2") { // tags: "kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.41 & !kotest.data.52"
            1 + 1 shouldBe 2
         }
         withData("thirdChildOfSecondChild1", "thirdChildOfSecondChild2") { // tags: "kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.41 & !kotest.data.49"
            withTests("firstAndOnlyChildOfThirdChildOfSecondChild1", "firstAndOnlyChildOfThirdChildOfSecondChild2") { // tags: "kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.41 & !kotest.data.49" - same as parent as no siblings
               1 + 1 shouldBe 2
            }
         }
      }
      withTests("thirdChild1", "thirdChild2") { // tags: "kotest.data.36 & !kotest.data.37 & !kotest.data.40"
         1 + 1 shouldBe 2
      }
   }
})
