package kotest.datatesttags

import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withContexts
import io.kotest.datatest.withData
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe

class DataTestTagsFreeSpec : FreeSpec({

   "parent context" - {
      "child context" - {
         withData("firstChildOfChildContext1", "firstChildOfChildContext2") { // tags: "(kotest.data.13) | !kotest.data" - ancestorTestPath: "parent context -- child context"
            withTests("firstChildOfFirstChildOfChildContext1", "firstChildOfFirstChildOfChildContext2") { // tags: "(kotest.data.13 & !kotest.data.17) | !kotest.data" - ancestorTestPath: "parent context -- child context"
               1 + 1 shouldBe 2
            }
            withData("secondChildOfFirstChildOfChildContext1", "secondChildOfFirstChildOfChildContext2") { // tags: "(kotest.data.13 & !kotest.data.14) | !kotest.data" - ancestorTestPath: "parent context -- child context"
               withTests("firstChildOfsecondChildOfFirstChildOfChildContext1", "firstChildOfsecondChildOfFirstChildOfChildContext2") { // tags: "(kotest.data.13 & !kotest.data.14) | !kotest.data" - ancestorTestPath: "parent context -- child context" (same as parent, no siblings)
                  1 + 1 shouldBe 2
               }
            }
         }
         withTests("secondChildOfChildContext1", "secondChildOfChildContext2") { // tags: "(kotest.data.23) | !kotest.data" - ancestorTestPath: "parent context -- child context"
            1 + 1 shouldBe 2
         }
      }
      withContexts("firstChildOfParentContext1", "firstChildOfParentContext2") { // tags: "(kotest.data.27) | !kotest.data" - ancestorTestPath: "parent context"
         1 + 1 shouldBe 2
      }
   }

   // ancestorTestPath: null for all below as they are not wrapped in a container
   withData("parent1", "parent2", "parent3") { // tag: "kotest.data.32"
      withTests("firstChild1", "firstChild2") { // tag: "kotest.data.32 & !kotest.data.36 & !kotest.data.54"
         1 + 1 shouldBe 2
      }
      withData("secondChild1", "secondChild2") { // tag: "kotest.data.32 & !kotest.data.33 & !kotest.data.54"
         withContexts("firstChildOfSecondChild1", "firstChildOfSecondChild2") { // tag: "kotest.data.32 & !kotest.data.33 & !kotest.data.54 & !kotest.data.45 & !kotest.data.48"
            withTests("firstChildOfFirstChildOfSecondChild1", "firstChildOfFirstChildOfSecondChild2") { // tags: "kotest.data.32 & !kotest.data.33 & !kotest.data.54 & !kotest.data.45 & !kotest.data.48 & !kotest.data.40 & !kotest.data.43"
               1 + 1 shouldBe 2
            }
            withTests("secondChildOfFirstChildOfSecondChild1", "secondChildOfFirstChildOfSecondChild2") { // tags: "kotest.data.32 & !kotest.data.33 & !kotest.data.54 & !kotest.data.45 & !kotest.data.48 & !kotest.data.40 & !kotest.data.43"
               1 + 1 shouldBe 2
            }
         }
         withTests("secondChildOfSecondChild1", "secondChildOfSecondChild2") { // tags: "kotest.data.32 & !kotest.data.33 & !kotest.data.54 & !kotest.data.37 & !kotest.data.48"
            1 + 1 shouldBe 2
         }
         withData("thirdChildOfSecondChild1", "thirdChildOfSecondChild2") { // tags: "kotest.data.32 & !kotest.data.33 & !kotest.data.54 & !kotest.data.37 & !kotest.data.45"
            withTests("firstAndOnlyChildOfThirdChildOfSecondChild1", "firstAndOnlyChildOfThirdChildOfSecondChild2") { // tags: "kotest.data.32 & !kotest.data.33 & !kotest.data.54 & !kotest.data.37 & !kotest.data.45" - same as parent as no siblings
               1 + 1 shouldBe 2
            }
         }
      }
      withTests("thirdChild1", "thirdChild2") { // tags: "kotest.data.32 & !kotest.data.33 & !kotest.data.36"
         1 + 1 shouldBe 2
      }
   }
})
