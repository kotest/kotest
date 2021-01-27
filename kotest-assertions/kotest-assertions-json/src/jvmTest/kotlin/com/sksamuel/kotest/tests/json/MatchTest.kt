package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.assertions.json.shouldNotMatchJson
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

class MatchTest : StringSpec() {

   private val json1 = """ { "name" : "sam", "location" : "london" } """
   private val json2 = """ { "location": "london", "name" : "sam" } """
   private val json3 = """ { "location": "chicago", "name" : "sam" } """

   init {
      "test json equality" {
         json1.shouldMatchJson(json2)
         json1.shouldNotMatchJson(json3)

         null.shouldMatchJson(null)
         null.shouldNotMatchJson(json1)
         json1.shouldNotMatchJson(null)

         shouldThrow<AssertionError> { null.shouldNotMatchJson(null) }
         shouldThrow<AssertionError> { null.shouldMatchJson(json1) }
         shouldThrow<AssertionError> { json1.shouldMatchJson(null) }
      }

      "test json equality with lenient mode" {

      }
   }
}
