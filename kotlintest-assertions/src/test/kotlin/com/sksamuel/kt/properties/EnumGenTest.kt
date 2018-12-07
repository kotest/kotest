package com.sksamuel.kt.properties

import io.kotlintest.properties.Gen
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

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