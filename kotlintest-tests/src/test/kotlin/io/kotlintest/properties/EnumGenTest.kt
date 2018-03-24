package io.kotlintest.properties

import io.kotlintest.runner.junit5.specs.StringSpec
import io.kotlintest.shouldBe

class EnumGenTest : StringSpec() {
  init {
    "enum gen should work generically" {
      val gen = Gen.enum<Weather>()
      gen.always().toSet() shouldBe
          setOf(Weather.Hot, Weather.Cold, Weather.Dry)
    }
  }
}

enum class Weather {
  Hot, Cold, Dry
}


