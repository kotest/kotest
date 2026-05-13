package com.sksamuel.kotest.property

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import org.opentest4j.AssertionFailedError

class PropertyAssertionFailedErrorTest : FunSpec({

   test("checkAll should propagate expected/actual values from cause to support IntelliJ diff") {
      val error = shouldThrow<AssertionFailedError> {
         checkAll(Arb.int(0..0)) { input ->
            input.shouldBeEqual(input + 1)
         }
      }
      // expected/actual should be propagated from the underlying matcher failure
      // so IntelliJ can render a "click to see diff" link on the outer error
      error.isExpectedDefined shouldBe true
      error.isActualDefined shouldBe true
      error.expected.value shouldNotBe null
      error.actual.value shouldNotBe null
   }

   test("checkAll should still work when cause has no expected/actual values") {
      val error = shouldThrow<AssertionFailedError> {
         checkAll(Arb.int(0..0)) {
            error("boom")
         }
      }
      error.isExpectedDefined shouldBe false
      error.isActualDefined shouldBe false
   }
})
