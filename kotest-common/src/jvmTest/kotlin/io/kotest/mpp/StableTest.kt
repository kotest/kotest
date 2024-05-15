package io.kotest.mpp

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.Row3
import io.kotest.matchers.shouldBe

class StableTest : FreeSpec({
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

   "Given a data class with generics" - {
      "When all members are stable, then the class should be considered stable" {
         val x = Row3("x", 1, 2.0)
         isStable(x::class, x) shouldBe true
      }

      "When isStable does not have access to actual values, it cannot determine the stability of the class" {
         val x = Row3("x", 1, 2.0)
         isStable(x::class, t = null) shouldBe false
      }

      "When an unstable type is included, the entire data class is considered unstable, even when including values" {
         val y: Row3<String, Int, Array<Int>> = Row3("x", 1, arrayOf(1, 2))
         isStable(y::class, y) shouldBe false
      }

      "When layering generic data classes, it should still work" {
         val stable = Pair(Pair("a", "b"), Pair("c", "d"))
         isStable(stable::class, stable) shouldBe true

         val unstable = Pair(Pair("a", "b"), Pair("c", arrayOf(1, 2)))
         isStable(unstable::class, unstable) shouldBe false
      }
   }
})
