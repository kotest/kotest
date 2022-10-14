package com.sksamuel.kotest.matchers.resource

import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.resource.shouldMatchResource
import io.kotest.matchers.resource.shouldNotMatchResource
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.io.File

class ResourceMatchersTest : ShouldSpec({

   context("shouldMatchResource") {

      should("should match resource") {
         val givenValue = "test\nresource\n"

         givenValue shouldMatchResource "/resourceMatchersTest/expected/testResource.txt"
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



   context("shouldNotMatchResource") {

      should("should not match resource") {
         val givenValue = "not a test resource"

         givenValue shouldNotMatchResource  "/resourceMatchersTest/expected/testResource.txt"
      }

      should("should return message with resource file path") {
         val givenValue = "test\nresource\n"

         val errorMessage = shouldThrow<AssertionError> {
            givenValue shouldNotMatchResource "/resourceMatchersTest/expected/testResource.txt"
         }.message ?: fail("Cannot get error message")

         errorMessage shouldContain "Expected : /resourceMatchersTest/expected/testResource.txt"
      }

   }

})

private fun String.fileFromRegex(fileRegex: Regex) =
   fileRegex
      .find(this)
      ?.value
      ?.substringAfter(": ")
      ?.let { File(it) }
      ?: fail("Cannot get file path from error message")
