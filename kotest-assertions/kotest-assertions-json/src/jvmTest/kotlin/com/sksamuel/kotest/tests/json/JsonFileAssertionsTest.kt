package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.shouldEqualJson
import io.kotest.assertions.json.shouldNotEqualJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class JsonFileAssertionsTest : FunSpec({
   val json1 = """ { "name" : "sam", "location" : "london" } """
   val json2 = """ { "location": "london", "name" : "sam" } """
   val json3 = """ { "location": "chicago", "name" : "sam" } """

   test("should return correct error message on failure") {
      shouldThrow<AssertionError> {
         withJsonTestFile(json1) shouldEqualJson json3
      }.message shouldBe """
         At 'location' expected 'chicago' but was 'london'

         expected:<{
           "location": "chicago",
           "name": "sam"
         }> but was:<{
           "name": "sam",
           "location": "london"
         }>
      """.trimIndent()
      shouldThrow<AssertionError> {
         withJsonTestFile(json1).toPath() shouldEqualJson json3
      }.message shouldBe """
         At 'location' expected 'chicago' but was 'london'

         expected:<{
           "location": "chicago",
           "name": "sam"
         }> but was:<{
           "name": "sam",
           "location": "london"
         }>
      """.trimIndent()

      shouldThrow<AssertionError> {
         withJsonTestFile(json1) shouldNotEqualJson json2
      }.message shouldBe """Expected values to not match"""

      shouldThrow<AssertionError> {
         withJsonTestFile(json1).toPath() shouldNotEqualJson json2
      }.message shouldBe """Expected values to not match"""
   }
})
