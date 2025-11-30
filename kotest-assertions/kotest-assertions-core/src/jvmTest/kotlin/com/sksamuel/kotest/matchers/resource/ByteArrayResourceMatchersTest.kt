package com.sksamuel.kotest.matchers.resource

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.print.StringPrint
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.EqualityMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.resource.resourceAsBytes
import io.kotest.matchers.resource.shouldMatchResource
import io.kotest.matchers.resource.shouldNotMatchResource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS

@Suppress("RegExpRepeatedSpace")
class ByteArrayResourceMatchersTest : ShouldSpec({

   context("shouldMatchResource") {

      should("byte array should match binary resource") {
         val givenValue = byteArrayOf(4, 2)

         givenValue shouldMatchResource "/resourceMatchersTest/expected/binary42.bin"
      }

      should("resource should match self") {
         val givenValue = resourceAsBytes("/resourceMatchersTest/expected/binary42.bin")

         givenValue shouldMatchResource "/resourceMatchersTest/expected/binary42.bin"
      }

      should("should return message with both resource and actual value files paths").config(enabled = !IS_OS_WINDOWS) {
         val givenValue = byteArrayOf(1, 2)

         val errorMessage = shouldThrow<AssertionError> {
            givenValue shouldMatchResource "/resourceMatchersTest/expected/binary42.bin"
         }.message ?: AssertionErrorBuilder.fail("Cannot get error message")

         errorMessage shouldContain "Expected : /resourceMatchersTest/expected/binary42.bin"
         errorMessage shouldContain "Actual   : .*/resourceMatchersTest/expected/_actual/binary42\\.bin".toRegex()
      }

      should("should write temp file with contents of actual value").config(enabled = !IS_OS_WINDOWS) {
         val givenValue = byteArrayOf(1, 2)

         val errorMessage = shouldThrow<AssertionError> {
            givenValue shouldMatchResource "/resourceMatchersTest/expected/binary42.bin"
         }.message ?: AssertionErrorBuilder.fail("Cannot get error message")

         val actualValueFile = errorMessage.fileFromRegex(
            "Actual   : .*/resourceMatchersTest/expected/_actual/binary42\\.bin".toRegex()
         )

         actualValueFile.shouldExist()
         actualValueFile.readBytes() shouldBe givenValue
      }

      should("include diff") {
         val givenValue = byteArrayOf(1, 2)

         val errorMessage = shouldThrow<AssertionError> {
            givenValue shouldMatchResource "/resourceMatchersTest/expected/binary42.bin"
         }.message ?: AssertionErrorBuilder.fail("Cannot get error message")

         errorMessage shouldContain "expected:<[4, 2]> but was:<[1, 2]"
      }

   }

   context("shouldMatchResource with custom matcher") {

      should("byte array should match binary resource") {
         val givenValue = byteArrayOf(2, 2, 2)

         givenValue.shouldMatchResource("/resourceMatchersTest/expected/binary42.bin", ::lastBytesMatch)
      }

      should("resource should match other resource") {
         val givenValue = resourceAsBytes("/resourceMatchersTest/expected/binary12.bin")

         givenValue.shouldMatchResource("/resourceMatchersTest/expected/binary42.bin", ::lastBytesMatch)
      }

   }

   context("shouldNotMatchResource") {

      should("byte array should not match binary resource") {
         val givenValue = byteArrayOf(1, 2)

         givenValue shouldNotMatchResource "/resourceMatchersTest/expected/binary42.bin"
      }

      should("resource should not match other resource") {
         val givenValue = resourceAsBytes("/resourceMatchersTest/expected/binary12.bin")

         givenValue shouldNotMatchResource "/resourceMatchersTest/expected/binary42.bin"
      }

      should("should return message with resource file path") {
         val givenValue = byteArrayOf(4, 2)

         val errorMessage = shouldThrow<AssertionError> {
            givenValue shouldNotMatchResource "/resourceMatchersTest/expected/binary42.bin"
         }.message ?: AssertionErrorBuilder.fail("Cannot get error message")

         errorMessage shouldContain "Expected : /resourceMatchersTest/expected/binary42.bin"
      }

   }

   context("shouldNotMatchResource with custom matcher") {

      should("byte should not match binary resource") {
         val givenValue = byteArrayOf(4)

         givenValue.shouldNotMatchResource("/resourceMatchersTest/expected/binary42.bin", ::lastBytesMatch)
      }

      should("resource should not match other resource") {
         val givenValue = resourceAsBytes("/resourceMatchersTest/expected/binary41.bin")

         givenValue.shouldNotMatchResource("/resourceMatchersTest/expected/binary42.bin", ::lastBytesMatch)
      }

   }

})

private fun lastBytesMatch(bytes: ByteArray) = object : Matcher<ByteArray> {
   override fun test(value: ByteArray): MatcherResult {
      val last = value.last()
      return EqualityMatcherResult(
         passed = last == bytes.last(),
         actual = StringPrint.printUnquoted(last.toString()),
         expected = StringPrint.printUnquoted(bytes.last().toString()),
         failureMessageFn = { "expected to match resource, but they differed" },
         negatedFailureMessageFn = { "expected not to match resource, but they match" },
      )
   }
}
