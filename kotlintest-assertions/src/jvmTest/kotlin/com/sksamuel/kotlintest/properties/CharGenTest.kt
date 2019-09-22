package com.sksamuel.kotlintest.properties

import io.kotlintest.properties.Gen
import io.kotlintest.properties.char
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class CharGenTest : FunSpec({
  test("should honour seed") {
    val seed: Long? = 1234909
    val seedListA = Gen.char().random(seed).take(120).toList()
    val seedListB = Gen.char().random(seed).take(120).toList()
    seedListA shouldBe seedListB
  }
})
