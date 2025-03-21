package io.kotest.permutations.seeds

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.paths.shouldNotExist
import io.kotest.matchers.shouldBe
import io.kotest.permutations.permutations
import io.kotest.property.PropertyTesting
import io.kotest.property.seed.seedDirectory
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import kotlin.io.path.readText

@EnabledIf(NotMacOnGithubCondition::class)
class PersistSeedsTest : FunSpec({

   fun clearSeedDirectory() {
      @OptIn(ExperimentalPathApi::class)
      seedDirectory().deleteRecursively()
   }

   beforeTest {
      PropertyTesting.writeFailedSeed = true
   }

   test("failed tests should persist seeds") {
      shouldThrowAny {
         permutations {
            seed = 2344324
            forEach {
               1 shouldBe 0
            }
         }
      }
      seedDirectory()
         .resolve("io.kotest.permutations.seeds.PersistSeedsTest_failed tests should persist seeds")
         .readText().shouldBe("2344324")
   }

   test("failed tests should persist seeds even for illegal chars in the test name ():<>/\\") {
      shouldThrowAny {
         permutations {
            seed = 623515
            forEach {
               1 shouldBe 0
            }
         }
      }
      seedDirectory()
         .resolve("io.kotest.permutations.seeds.PersistSeedsTest_failed tests should persist seeds even for illegal chars in the test name _______")
         .readText().shouldBe("623515")
   }

   test("when write seeds is disabled, failed tests should not persist seeds") {
      clearSeedDirectory()
      shouldThrowAny {
         permutations {
            writeFailedSeed = false
            seed = 12345
            forEach { error("boom") }
         }
      }
      seedDirectory().shouldNotExist()
   }

   test("a successful test should not write seed") {
      clearSeedDirectory()
      permutations {
         seed = 12345
         forEach { }
      }
      seedDirectory().shouldNotExist()
   }
})
