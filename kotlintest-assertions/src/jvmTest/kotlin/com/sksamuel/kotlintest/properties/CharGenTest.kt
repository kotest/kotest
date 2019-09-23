package com.sksamuel.kotlintest.properties

import io.kotlintest.properties.*
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec

class CharGenTest : FunSpec({
  test("should honour seed") {
    val seed: Long? = 1234909
    val seedListA = Gen.char().random(seed).take(120).toList()
    val seedListB = Gen.char().random(seed).take(120).toList()
    seedListA shouldBe seedListB
  }

  test("test gen chars")  {
     println(Gen.char().take(50).toCharArray().joinToString())
     println(Gen.char(CHR_ARABIC + CHR_RUNIC).take(50).joinToString(separator = ""))
     println(Gen.char(CHR_GEOMETRIC_SHAPES).take(50).joinToString(separator = ""))
     println(Gen.char(CHR_MATH_OPERATORS).take(50).joinToString(separator = ""))
     println(Gen.char(CHR_KANGXI_RADICALS).take(50).joinToString(separator = ""))
     println(Gen.char(CHR_CYRILLIC + CHR_CYRILLIC_SUPPLMNT).take(50).joinToString(separator = ""))
  }
})
