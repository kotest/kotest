package com.sksamuel.kotest

import io.kotest.assertions.Actual
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.Expected
import io.kotest.assertions.print.Printed
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AssertionErrorBuilderTest : FunSpec() {
   init {
      test("comparison values should be formatted in the intellij way") {
         val expected = "expected:<foo> but was:<bar>"
         val actual = AssertionErrorBuilder.create().withValues(Expected(Printed("foo")), Actual(Printed("bar"))).build().message
         expected shouldBe actual
      }
   }
}
