package com.sksamuel.kotest.property.exhaustive

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.property.Exhaustive
import io.kotest.property.RandomSource
import io.kotest.property.exhaustive.andNull
import io.kotest.property.exhaustive.ints

@EnabledIf(LinuxCondition::class)
class ExhaustiveTest : FunSpec() {
   init {
      test("andNull should include null in the generated values") {
         Exhaustive.ints(0..100).andNull().values.map { it }.shouldContain(null)
      }

      test("should throws on generate values in a empty exhaustive") {
         val exhaustive = object : Exhaustive<String>() {
            override val values = emptyList<String>()
         }

         shouldThrow<IllegalStateException> {
            exhaustive.generate(RandomSource.default())
         }
      }
   }
}
