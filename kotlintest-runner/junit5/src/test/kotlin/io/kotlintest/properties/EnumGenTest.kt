package io.kotlintest.properties

import io.kotlintest.shouldBe
import io.kotlintest.specs.AbstractStringSpec

class EnumGenTest : AbstractStringSpec() {
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


