package io.kotest.property

import io.kotest.property.progression.constant
import io.kotest.shouldBe
import io.kotest.shouldThrowAny
import io.kotest.specs.FunSpec

class PropertyExceptionTest : FunSpec() {
   init {
      test("exception in a property test should report failing value") {
         shouldThrowAny {
            checkAll(Progression.constant(1), Progression.constant(true)) { _, _ ->
               throw RuntimeException("foo")
            }
         }.message shouldBe """Property failed after 1 attempts
Caused by RuntimeException: foo"""
      }
   }
}
