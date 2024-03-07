package com.sksamuel.kotest.matchers.resource

import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.be
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.resource.resourceAsString
import io.kotest.matchers.resource.shouldMatchResource
import io.kotest.matchers.resource.shouldNotMatchResource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.startWith
import java.io.File

@Suppress("RegExpRepeatedSpace")
class StringResourceMatchersTest : ShouldSpec({

   context("shouldMatchResource") {

      should("should match resource") {
         val givenValue = "test\nresource\n"

         givenValue shouldMatchResource "/resourceMatchersTest/expected/testResource.txt"
      }

      should("should match binary resource as string") {
         val givenValue = String(byteArrayOf(4, 2))

         givenValue shouldMatchResource "/resourceMatchersTest/expected/binary42.bin"
      }

      should("should return message with both resource and actual value files paths") {
         val givenValue = "not a test resource"

         val errorMessage = shouldThrow<AssertionError> {
            givenValue shouldMatchResource "/resourceMatchersTest/expected/testResource.txt"
         }.message ?: fail("Cannot get error message")

         errorMessage shouldContain "Expected : /resourceMatchersTest/expected/testResource.txt"
         errorMessage shouldContain "Actual   : .*/resourceMatchersTest/expected/_actual/testResource\\.txt".toRegex()
      }

      should("should write temp file with contents of actual value") {
         val givenValue = "not a test resource"

         val errorMessage = shouldThrow<AssertionError> {
            givenValue shouldMatchResource "/resourceMatchersTest/expected/testResource.txt"
         }.message ?: fail("Cannot get error message")

         val actualValueFile = errorMessage.fileFromRegex(
            "Actual   : .*/resourceMatchersTest/expected/_actual/testResource\\.txt".toRegex()
         )

         actualValueFile.shouldExist()
         actualValueFile.readText() shouldBe givenValue
      }

   }

   context("shouldMatchResource with custom matcher") {

      should("should match resource") {
         val givenValue = "test\nresource\nsomething"

         givenValue.shouldMatchResource("/resourceMatchersTest/expected/testResource.txt", ::startWith)
         givenValue.shouldMatchResource("/resourceMatchersTest/expected/testResource.txt",
            { s -> startWith(s.lowercase()) })
      }

      should("should return message with both resource and actual value files paths") {
         val givenValue = "not a test resource"

         val errorMessage = shouldThrow<AssertionError> {
            givenValue.shouldMatchResource("/resourceMatchersTest/expected/testResource.txt", ::startWith)
         }.message ?: fail("Cannot get error message")

         errorMessage shouldContain "Expected : /resourceMatchersTest/expected/testResource.txt"
         errorMessage shouldContain "Actual   : .*/resourceMatchersTest/expected/_actual/testResource\\.txt".toRegex()
      }
   }

   context("shouldNotMatchResource") {

      should("should not match resource") {
         val givenValue = "not a test resource"

         givenValue shouldNotMatchResource "/resourceMatchersTest/expected/testResource.txt"
      }

      should("should not match binary resource as string") {
         val givenValue = "not a test resource"

         givenValue shouldNotMatchResource "/resourceMatchersTest/expected/binary42.bin"
      }

      should("should return message with resource file path") {
         val givenValue = "test\nresource\n"

         val errorMessage = shouldThrow<AssertionError> {
            givenValue shouldNotMatchResource "/resourceMatchersTest/expected/testResource.txt"
         }.message ?: fail("Cannot get error message")

         errorMessage shouldContain "Expected : /resourceMatchersTest/expected/testResource.txt"
      }

   }

   context("shouldNotMatchResource with custom matcher") {

      should("should not match resource") {
         val givenValue = "not a test resource"

         givenValue.shouldNotMatchResource("/resourceMatchersTest/expected/testResource.txt", ::startWith)
      }

      should("should return message with resource file path") {
         val givenValue = "test\nresource\n"

         val errorMessage = shouldThrow<AssertionError> {
            givenValue.shouldNotMatchResource("/resourceMatchersTest/expected/testResource.txt", ::startWith)
         }.message ?: fail("Cannot get error message")

         errorMessage shouldContain "Expected : /resourceMatchersTest/expected/testResource.txt"
      }
   }

   context("line separators tests") {
      val givenCR = "test\rresource\r"
      val givenLF = "test\nresource\n"
      val givenCRLF = "test\r\nresource\r\n"

      val resourceCR = "/resourceMatchersTest/expected/testResource_cr.txt"
      val resourceLF = "/resourceMatchersTest/expected/testResource.txt"
      val resourceCRLF = "/resourceMatchersTest/expected/testResource_crlf.txt"

      should("Check if line endings are as expected") {
         givenCR shouldBe resourceAsString(resourceCR)
         givenLF shouldBe resourceAsString(resourceLF)
         givenCRLF shouldBe resourceAsString(resourceCRLF)
      }

      should("should match ignoring newline separator differences") {
         givenCR shouldMatchResource resourceLF
         givenCR shouldMatchResource resourceCRLF

         givenLF shouldMatchResource resourceCR
         givenLF shouldMatchResource resourceCRLF

         givenCRLF shouldMatchResource resourceCR
         givenCRLF shouldMatchResource resourceLF
      }

      should("should match ignoring newline separator differences with custom matcher") {
         givenCR.shouldMatchResource(resourceLF, ::startWith)
         givenCR.shouldMatchResource(resourceCRLF, ::startWith)

         givenLF.shouldMatchResource(resourceCR, ::startWith)
         givenLF.shouldMatchResource(resourceCRLF, ::startWith)

         givenCRLF.shouldMatchResource(resourceCR, ::startWith)
         givenCRLF.shouldMatchResource(resourceLF, ::startWith)
      }

      should("should not match aware of newline separator differences with custom matcher") {
         givenCR.shouldNotMatchResource(resourceLF, ::be, ignoreLineSeparators = false)
         givenCR.shouldNotMatchResource(resourceCRLF, ::be, ignoreLineSeparators = false)

         givenLF.shouldNotMatchResource(resourceCR, ::be, ignoreLineSeparators = false)
         givenLF.shouldNotMatchResource(resourceCRLF, ::be, ignoreLineSeparators = false)

         givenCRLF.shouldNotMatchResource(resourceCR, ::be, ignoreLineSeparators = false)
         givenCRLF.shouldNotMatchResource(resourceLF, ::be, ignoreLineSeparators = false)
      }
   }

})

fun String.fileFromRegex(fileRegex: Regex) =
   fileRegex
      .find(this)
      ?.value
      ?.substringAfter(": ")
      ?.let { File(it) }
      ?: fail("Cannot get file path from error message")
