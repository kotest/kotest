package io.kotlintest.properties

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class EnumGenTest : StringSpec() {
  init {
    "enum gen should work generically" {
      val gen = Gen.oneOf<Weather>()
      (1..1000).map { gen.generate() }.toSet() shouldBe
          setOf(Weather.Hot, Weather.Cold, Weather.Dry)
    }
  }
}

enum class Weather {
  Hot, Cold, Dry
}


