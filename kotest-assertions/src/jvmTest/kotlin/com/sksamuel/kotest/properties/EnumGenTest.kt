package com.sksamuel.kotest.properties

import io.kotest.properties.Gen
import io.kotest.properties.enum
import io.kotest.shouldBe
import io.kotest.specs.StringSpec

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
