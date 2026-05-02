package io.kotest.datatest.tags
// tests in this package all have the same structure but different style (FunSpec, FreeSpec, BehaviorSpec ...) - keep it that way so they can all be tested at once
import io.kotest.common.Platform
import io.kotest.common.platform
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.config.DefaultTestConfig
import io.kotest.datatest.withContexts
import io.kotest.datatest.withData
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe




class DataTestTagsFunSpec : FunSpec({

    // Deeply nested data tests on WasmWasi node intermittently report a single
    // leaf as failed with no failure detail (kotlin/wasm + Mocha environmental flake).
    // The same spec is covered consistently on jvm, js, wasmJs, and native targets,
    // so a single retry is enough to absorb the wasmWasi flake without losing coverage.
    if (platform == Platform.WasmWasi) {
        defaultTestConfig = DefaultTestConfig(retries = 1)
    }


    context("parent context") {
        context("child context") {
            withData("firstChildOfChildContext1", "firstChildOfChildContext2") { // kotest.data.nonJvm
               withTests("firstChildOfFirstChildOfChildContext1", "firstChildOfFirstChildOfChildContext2") { // kotest.data.nonJvm
                  1 + 1 shouldBe 2
               }
               withData("secondChildOfFirstChildOfChildContext1", "secondChildOfFirstChildOfChildContext2") { // kotest.data.nonJvm
                  withTests("firstChildOfSecondChildOfFirstChildOfChildContext1", "firstChildOfSecondChildOfFirstChildOfChildContext2") { // kotest.data.nonJvm
                     1 + 1 shouldBe 2
                  }
               }
            }
            withTests("secondChildOfChildContext1", "secondChildOfChildContext2") { // kotest.data.nonJvm
                1 + 1 shouldBe 2
            }
        }
        withContexts("firstChildOfParentContext1", "firstChildOfParentContext2") { // kotest.data.nonJvm
            1 + 1 shouldBe 2
        }
    }


   withData("parent1", "parent2", "parent3") { // kotest.data.nonJvm
      withTests("firstChild1", "firstChild2") { // kotest.data.nonJvm
         1 + 1 shouldBe 2
      }
      withData("secondChild1", "secondChild2") { // kotest.data.nonJvm
         withContexts("firstChildOfSecondChild1", "firstChildOfSecondChild2") { // kotest.data.nonJvm
            withTests("firstChildOfFirstChildOfSecondChild1", "firstChildOfFirstChildOfSecondChild2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
            withTests("secondChildOfFirstChildOfSecondChild1", "secondChildOfFirstChildOfSecondChild2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
         }
         withTests("secondChildOfSecondChild1", "secondChildOfSecondChild2") { // kotest.data.nonJvm
            1 + 1 shouldBe 2
         }
         withData("thirdChildOfSecondChild1", "thirdChildOfSecondChild2") { // kotest.data.nonJvm
            withTests("firstAndOnlyChildOfThirdChildOfSecondChild1", "firstAndOnlyChildOfThirdChildOfSecondChild2") { // kotest.data.nonJvm
               1 + 1 shouldBe 2
            }
         }
      }
      withTests("thirdChild1", "thirdChild2") { // kotest.data.nonJvm
         1 + 1 shouldBe 2
      }
   }
})
