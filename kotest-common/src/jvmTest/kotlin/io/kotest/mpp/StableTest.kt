package io.kotest.mpp

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class StableTest : StringSpec({
   "class should be correctly identified as stable" {
      data class StableClass(
         val string: String,
         val int: Int,
         val long: Long,
         val double: Double,
         val float: Float,
         val byte: Byte,
         val short: Short,
         val boolean: Boolean,
         val pair: Pair<String, String>,
         val triple: Triple<String, String, String>,
         val char: Char,
      )

      class Unstable() {
         // something unstable here
      }

      data class UnstableDataClass(val string: String, val unstable: Unstable)

      isStable(StableClass::class) shouldBe true
      isStable(Unstable::class) shouldBe false
      isStable(UnstableDataClass::class) shouldBe false
   }
})
