package com.sksamuel.kotest.engine.test

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.mockk.mockk

class MockedExceptionTest : FunSpec() {
   init {
      test("shouldThrow should catch mocked exception") {
         shouldThrow<IllegalStateException> {
            throw mockk<ArithmeticException>()
         }
      }
   }
}
