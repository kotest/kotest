package com.sksamuel.kotlintest.properties

import io.kotlintest.properties.Gen
import io.kotlintest.properties.assertAll
import io.kotlintest.properties.bool
import io.kotlintest.properties.int
import io.kotlintest.properties.long
import io.kotlintest.properties.string
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

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
