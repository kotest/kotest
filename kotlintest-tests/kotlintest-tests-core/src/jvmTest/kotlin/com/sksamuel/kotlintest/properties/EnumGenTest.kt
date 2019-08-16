package com.sksamuel.kotlintest.properties

import io.kotlintest.properties.Gen
import io.kotlintest.specs.StringSpec
import io.kotlintest.shouldBe

class EnumGenTest : StringSpec() {
  init {
    "enum gen should work generically" {
      val gen = Gen.enum<Weather>()
      gen.constants().toSet() shouldBe
          setOf(Weather.Hot, Weather.Cold, Weather.Dry)
    }
  }
}

enum class Weather {
  Hot, Cold, Dry
}