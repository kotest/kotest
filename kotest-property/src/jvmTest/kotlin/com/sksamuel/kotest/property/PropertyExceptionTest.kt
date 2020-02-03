package com.sksamuel.kotest.property

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.Exhaustive
import io.kotest.property.exhaustive.single

class PropertyExceptionTest : FunSpec() {
   init {
      test("exception in a property test should report failing value") {
         shouldThrowAny {
            checkAll(Exhaustive.single(1), Exhaustive.single(true)) { _, _ ->
               throw RuntimeException("foo")
            }
         }.message shouldBe """Property failed after 1 attempts
Caused by RuntimeException: foo"""
      }
   }
}
