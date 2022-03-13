package com.sksamuel.kotest.matchers.reflection

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.reflection.compose
import io.kotest.matchers.shouldBe

class ComposeTest : StringSpec() {
   data class Person(
      val name: String,
      val age: Int,
      val address: Address
   )

   data class Address(
      val city: String,
      val street: String,
      val buildingNumber: String
   )

   private val nameMatcher = Matcher<String> {
      MatcherResult(
         value == "John",
         { "Name $value should be John" },
         { "Name $value should not be John" }
      )
   }

   private val ageMatcher = Matcher<Int> {
      MatcherResult(
         value == 10,
         { "Age $value should be 10" },
         { "Age $value should not be 10" }
      )
   }

   private val addressMatcher = Matcher<Address> {
      MatcherResult(
         value == Address("Warsaw", "Test", "1/1"),
         { "Address $value should be Test 1/1 Warsaw" },
         { "Address $value should not be Test 1/1 Warsaw" }
      )
   }

   init {
      "Person matcher compose test" {
         val matcherResult = Matcher.compose(
            nameMatcher to Person::name,
            ageMatcher to Person::age,
            addressMatcher to Person::address
         )
            .test(Person("John", 10, Address("Warsaw", "Test", "1/1")))

         matcherResult.passed() shouldBe true
         matcherResult.failureMessage() shouldBe """
            Name John should be John
            Age 10 should be 10
            Address ${Address("Warsaw", "Test", "1/1")} should be Test 1/1 Warsaw
         """.trimIndent()
         matcherResult.negatedFailureMessage() shouldBe """
            Name John should not be John
            Age 10 should not be 10
            Address ${Address("Warsaw", "Test", "1/1")} should not be Test 1/1 Warsaw
         """.trimIndent()
      }
   }
}
