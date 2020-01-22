package io.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.exhaustive.constant
import io.kotest.shouldBe
import io.kotest.shouldThrowAny

class PropertyExceptionTest : FunSpec() {
   init {
      test("exception in a property test should report failing value") {
         shouldThrowAny {
            checkAll(Exhaustive.constant(1), Exhaustive.constant(true)) { _, _ ->
               throw RuntimeException("foo")
            }
         }.message shouldBe """Property failed after 1 attempts
Caused by RuntimeException: foo"""
      }
   }
}
