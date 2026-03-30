package kotest.datatesttags
// tests in this package all have the same structure but different style (FunSpec, FreeSpec, BehaviorSpec ...) - keep it that way so they can all be tested at once
import io.kotest.core.spec.style.WordSpec
import io.kotest.datatest.withWhens
import io.kotest.datatest.withShoulds
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe




class DataTestTagsWordSpec : WordSpec({

   "parent context" When {
      "child context" When {
         withWhens("firstChildOfChildContext1", "firstChildOfChildContext2") { // tags: "((kotest.data.16) | !kotest.data) | kotest.data.nonJvm" - ancestorTestPath: "parent context -- child context"
            withShoulds("firstChildOfFirstChildOfChildContext1", "firstChildOfFirstChildOfChildContext2") { // tags: "((kotest.data.16 & !kotest.data.20) | !kotest.data) | kotest.data.nonJvm" - ancestorTestPath: "parent context -- child context"
               1 + 1 shouldBe 2
            }
            withData("secondChildOfFirstChildOfChildContext1", "secondChildOfFirstChildOfChildContext2") { // tags: "((kotest.data.16 & !kotest.data.17) | !kotest.data) | kotest.data.nonJvm" - ancestorTestPath: "parent context -- child context"
               withShoulds("firstChildOfSecondChildOfFirstChildOfChildContext1", "firstChildOfSecondChildOfFirstChildOfChildContext2") { // tags: "((kotest.data.16 & !kotest.data.17) | !kotest.data) | kotest.data.nonJvm" - ancestorTestPath: "parent context -- child context" (same as parent, no siblings)
                  1 + 1 shouldBe 2
               }
            }
         }
         withShoulds("secondChildOfChildContext1", "secondChildOfChildContext2") { // tags: "((kotest.data.26) | !kotest.data) | kotest.data.nonJvm" - ancestorTestPath: "parent context -- child context"
            1 + 1 shouldBe 2
         }
      }
      withWhens("firstChildOfParentContext1", "firstChildOfParentContext2") { // tags: "((kotest.data.30) | !kotest.data) | kotest.data.nonJvm" - ancestorTestPath: "parent context"
         1 + 1 shouldBe 2
      }
   }

   // ancestorTestPath: null for all below as they are not wrapped in a container
   withData("parent1", "parent2", "parent3") { // tag: "(kotest.data.36) | kotest.data.nonJvm"
      withWhens("firstChild1", "firstChild2") { // tag: "(kotest.data.36 & !kotest.data.40 & !kotest.data.58) | kotest.data.nonJvm"
         1 + 1 shouldBe 2
      }
      withWhens("secondChild1", "secondChild2") { // tag: "(kotest.data.36 & !kotest.data.37 & !kotest.data.58) | kotest.data.nonJvm"
         withWhens("firstChildOfSecondChild1", "firstChildOfSecondChild2") { // tag: "(kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.49 & !kotest.data.52) | kotest.data.nonJvm"
            withShoulds("firstChildOfFirstChildOfSecondChild1", "firstChildOfFirstChildOfSecondChild2") { // tags: "(kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.49 & !kotest.data.52 & !kotest.data.45) | kotest.data.nonJvm"
               1 + 1 shouldBe 2
            }
            withShoulds("secondChildOfFirstChildOfSecondChild1", "secondChildOfFirstChildOfSecondChild2") { // tags: "(kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.49 & !kotest.data.52 & !kotest.data.42) | kotest.data.nonJvm"
               1 + 1 shouldBe 2
            }
         }
         withShoulds("secondChildOfSecondChild1", "secondChildOfSecondChild2") { // tags: "(kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.41 & !kotest.data.52) | kotest.data.nonJvm"
            1 + 1 shouldBe 2
         }
         withData("thirdChildOfSecondChild1", "thirdChildOfSecondChild2") { // tags: "(kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.41 & !kotest.data.49) | kotest.data.nonJvm"
            withShoulds("firstAndOnlyChildOfThirdChildOfSecondChild1", "firstAndOnlyChildOfThirdChildOfSecondChild2") { // tags: "(kotest.data.36 & !kotest.data.37 & !kotest.data.58 & !kotest.data.41 & !kotest.data.49) | kotest.data.nonJvm" - same as parent as no siblings
               1 + 1 shouldBe 2
            }
         }
      }
      withShoulds("thirdChild1", "thirdChild2") { // tags: "(kotest.data.36 & !kotest.data.37 & !kotest.data.40) | kotest.data.nonJvm"
         1 + 1 shouldBe 2
      }
   }
})
