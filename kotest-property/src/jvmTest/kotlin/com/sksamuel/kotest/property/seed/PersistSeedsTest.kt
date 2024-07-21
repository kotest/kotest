package com.sksamuel.kotest.property.seed

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.paths.shouldNotExist
import io.kotest.matchers.shouldBe
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyTesting
import io.kotest.property.checkAll
import io.kotest.property.seed.seedDirectory
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively
import kotlin.io.path.readText

class PersistSeedsTest : FunSpec({

   fun clearSeedDirectory() {
      @OptIn(ExperimentalPathApi::class)
      seedDirectory().deleteRecursively()
   }

   afterTest {
      PropertyTesting.writeFailedSeed = true
   }

   test("failed tests should persist seeds") {
      shouldThrowAny {
         checkAll<Int, Int>(PropTestConfig(seed = 2344324)) { a, _ ->
            a shouldBe 0
         }
      }
      seedDirectory()
         .resolve("com.sksamuel.kotest.property.seed.PersistSeedsTest_failed tests should persist seeds")
         .readText().shouldBe("2344324")
   }

   test("failed tests should persist seeds even for illegal chars ():<>/\\") {
      shouldThrowAny {
         checkAll<Int, Int>(PropTestConfig(seed = 623515)) { a, _ ->
            a shouldBe 0
         }
      }
      seedDirectory()
         .resolve("com.sksamuel.kotest.property.seed.PersistSeedsTest_failed tests should persist seeds even for illegal chars _______")
         .readText().shouldBe("623515")
   }

   test("when write seeds is disabled, failed tests should not persist seeds") {
      clearSeedDirectory()
      PropertyTesting.writeFailedSeed = false
      shouldThrowAny {
         checkAll<Int, Int> { a, b ->
            a shouldBe b
         }
      }
      seedDirectory().shouldNotExist()
   }

   test("a successful test should not write seed") {
      clearSeedDirectory()
      checkAll<Int, Int> { _, _ -> }
      seedDirectory().shouldNotExist()
   }
})
