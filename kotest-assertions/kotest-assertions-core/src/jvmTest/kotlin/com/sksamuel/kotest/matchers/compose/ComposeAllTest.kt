package com.sksamuel.kotest.matchers.compose

import io.kotest.assertions.shouldFailWithMessage
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.compose.all
import io.kotest.matchers.reflection.havingProperty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.contain
import io.kotest.matchers.string.containADigit

class ComposeAllTest : StringSpec() {
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
      "Person Matcher.all test" {

         val matcherResult = Matcher.all(
            havingProperty(nameMatcher to Person::name),
            havingProperty(ageMatcher to Person::age),
            havingProperty(addressMatcher to Person::address)
         ).test(Person("John", 10, Address("Warsaw", "Test", "1/1")))

         matcherResult.passed() shouldBe true
         matcherResult.failureMessage() shouldBe ""
         matcherResult.negatedFailureMessage() shouldBe """
            Name John should not be John
            Age 10 should not be 10
            Address ${Address("Warsaw", "Test", "1/1")} should not be Test 1/1 Warsaw
         """.trimIndent()
      }

      "Matcher.all should filter out successful matchers when using properties" {

         val matcherResult = Matcher.all(
            havingProperty(nameMatcher to Person::name),
            havingProperty(ageMatcher to Person::age),
            havingProperty(addressMatcher to Person::address)
         ).test(Person("John2", 10, Address("Warsaw2", "Test2", "1/1")))

         matcherResult.passed() shouldBe false
         matcherResult.failureMessage() shouldBe """
            Name John2 should be John
            Address ${Address("Warsaw2", "Test2", "1/1")} should be Test 1/1 Warsaw
         """.trimIndent()
         matcherResult.negatedFailureMessage() shouldBe """
            Age 10 should not be 10
         """.trimIndent()
      }

      "Matcher.all should filter out successful matchers when using matchers" {

         val matcherResult = Matcher.all<Person>(
            nameMatcher.contramap { it.name },
            ageMatcher.contramap { it.age },
            addressMatcher.contramap { it.address },
         ).test(Person("John2", 10, Address("Warsaw2", "Test2", "1/1")))

         matcherResult.passed() shouldBe false
         matcherResult.failureMessage() shouldBe """
            Name John2 should be John
            Address ${Address("Warsaw2", "Test2", "1/1")} should be Test 1/1 Warsaw
         """.trimIndent()
         matcherResult.negatedFailureMessage() shouldBe """
            Age 10 should not be 10
         """.trimIndent()
      }

      "Person Matcher.all test, should fail on mismatching types" {
         shouldFailWithMessage(
            "Mismatching type of matcher for property name: " +
               "class java.lang.String cannot be cast to class java.lang.Number " +
               "(java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap')"
         ) {
            Matcher.all(
               havingProperty(ageMatcher to Person::name),
               havingProperty(ageMatcher to Person::age),
               havingProperty(addressMatcher to Person::address)
            ).test(Person("John", 10, Address("Warsaw", "Test", "1/1")))
         }
      }

      "password Matcher.all test" {

         val passwordMatcher = Matcher.all(
            containADigit(), contain(Regex("[a-z]")), contain(Regex("[A-Z]"))
         )

         val matcherResult = passwordMatcher.test("StrongPassword123")

         matcherResult.passed() shouldBe true
         matcherResult.failureMessage() shouldBe ""
         matcherResult.negatedFailureMessage() shouldBe """
            "StrongPassword123" should not contain any digits
            "StrongPassword123" should not contain regex [a-z]
            "StrongPassword123" should not contain regex [A-Z]
         """.trimIndent()
      }
   }
}
