package com.sksamuel.kotest.property.seed

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.paths.shouldBeEmptyDirectory
import io.kotest.matchers.shouldBe
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyTesting
import io.kotest.property.checkAll
import io.kotest.property.seed.seedDirectory
import kotlin.io.path.readText

class PersistSeedsTest : FunSpec({

   test("failed tests should persist seeds") {
      shouldThrowAny {
         checkAll<Int, Int>(PropTestConfig(seed = 2344324)) { a, b ->
            a shouldBe 0
         }
      }
      seedDirectory()
         .resolve("com.sksamuel.kotest.property.seed.PersistSeedsTest")
         .resolve("failed tests should persist seeds")
         .readText().shouldBe("2344324")
   }

   test("when write seeds is disabled, failed tests should not persist seeds") {
      seedDirectory().apply {
         toFile().listFiles().forEach { it.deleteRecursively() }
      }
      PropertyTesting.writeFailedSeed = false
      shouldThrowAny {
         checkAll<Int, Int> { a, b ->
            a shouldBe b
         }
      }
      seedDirectory().shouldBeEmptyDirectory()
   }

})
