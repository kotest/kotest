package com.sksamuel.kotest.properties

import io.kotest.properties.Gen
import io.kotest.properties.assertAll
import io.kotest.properties.bool
import io.kotest.properties.int
import io.kotest.properties.long
import io.kotest.properties.string
import io.kotest.shouldBe
import io.kotest.specs.FunSpec

class GenSeedTest : FunSpec() {
   init {
      test("seeds should result in consistent randoms") {
         assertAll(Gen.long()) { seed ->
            Gen.int().random(seed).take(100).toList() shouldBe Gen.int().random(seed).take(100).toList()
            Gen.long().random(seed).take(100).toList() shouldBe Gen.long().random(seed).take(100).toList()
            Gen.string().random(seed).take(100).toList() shouldBe Gen.string().random(seed).take(100).toList()
            Gen.bool().random(seed).take(100).toList() shouldBe Gen.bool().random(seed).take(100).toList()
         }
      }
   }
}
