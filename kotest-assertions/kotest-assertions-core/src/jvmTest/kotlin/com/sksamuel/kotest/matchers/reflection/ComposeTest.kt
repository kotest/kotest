package com.sksamuel.kotest.matchers.reflection

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.compose.compose
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.contain
import io.kotest.matchers.string.containADigit

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
         it == "John",
         { "Name $it should be John" },
         { "Name $it should not be John" }
      )
   }

   private val ageMatcher = Matcher<Int> {
      MatcherResult(
         it == 10,
         { "Age $it should be 10" },
         { "Age $it should not be 10" }
      )
   }

   private val addressMatcher = Matcher<Address> {
      MatcherResult(
         it == Address("Warsaw", "Test", "1/1"),
         { "Address $it should be Test 1/1 Warsaw" },
         { "Address $it should not be Test 1/1 Warsaw" }
      )
   }

   init {
      "Person matcher compose test" {

         val matcherResult = Matcher.compose(
            nameMatcher to Person::name,
            ageMatcher to Person::age,
            addressMatcher to Person::address
         ).test(Person("John", 10, Address("Warsaw", "Test", "1/1")))

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

      "password matcher test" {

         val passwordMatcher = Matcher.compose(
            containADigit(), contain(Regex("[a-z]")), contain(Regex("[A-Z]"))
         )

         val matcherResult = passwordMatcher.test("StrongPassword123")

         matcherResult.passed() shouldBe true
         matcherResult.failureMessage() shouldBe """
            "StrongPassword123" should contain at least one digit
            "StrongPassword123" should contain regex [a-z]
            "StrongPassword123" should contain regex [A-Z]
         """.trimIndent()
         matcherResult.negatedFailureMessage() shouldBe """
            "StrongPassword123" should not contain any digits
            "StrongPassword123" should not contain regex [a-z]
            "StrongPassword123" should not contain regex [A-Z]
         """.trimIndent()
      }
   }
}
