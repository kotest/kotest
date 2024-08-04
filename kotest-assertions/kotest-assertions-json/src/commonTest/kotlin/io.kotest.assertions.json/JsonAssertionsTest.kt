package io.kotest.assertions.json

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class JsonAssertionsTest : StringSpec({

   val json1 = """ { "name" : "sam", "location" : "london" } """
   val json2 = """ { "location": "london", "name" : "sam" } """
   val json3 = """ { "location": "chicago", "name" : "sam" } """

   "should return correct error message on failure" {
      shouldThrow<AssertionError> {
         json1 shouldEqualJson json3
      }.message shouldBe """expected json to match, but they differed
         |
         |expected:<{"location":"chicago","name":"sam"}> but was:<{"name":"sam","location":"london"}>""".trimMargin()

      shouldThrow<AssertionError> {
         json1 shouldNotEqualJson json2
      }.message shouldBe """expected not to match with: {"location":"london","name":"sam"} but match: {"name":"sam","location":"london"}"""
   }
})
