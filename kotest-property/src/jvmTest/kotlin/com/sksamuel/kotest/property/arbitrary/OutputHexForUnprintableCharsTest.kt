package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.AssertionFailedError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.ascii
import io.kotest.property.arbitrary.printableAscii
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.property.internal.escapeUnprintable

@OptIn(ExperimentalKotest::class)
class OutputHexForUnprintableCharsTest : StringSpec({

   "should handle unprintable characters in failure messages" {
      checkAll(
         PropTestConfig(outputHexForUnprintableChars = true, seed = -5556043119863981334),
         Arb.string(1..127, codepoints = Codepoint.ascii())
      ) { str ->
         val message = shouldThrow<AssertionFailedError> { str shouldBe "expectedString" }.message

         val escapedMessage = message?.escapeUnprintable()

         val expectedEscapedString = str.escapeUnprintable()
         escapedMessage shouldContain expectedEscapedString
         escapedMessage shouldContain "expectedString"
      }
   }

   "should handle printable characters without escaping" {
      checkAll(
         PropTestConfig(outputHexForUnprintableChars = true, seed = -5556043119863981334),
         Arb.string(1..127, codepoints = Codepoint.printableAscii())
      ) { str ->
         val message = shouldThrow<AssertionFailedError> { str shouldBe "expectedString" }.message
         message?.escapeUnprintable() shouldBe message
      }
   }
})
