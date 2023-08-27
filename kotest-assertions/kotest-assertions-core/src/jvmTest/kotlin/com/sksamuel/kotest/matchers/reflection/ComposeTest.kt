package com.sksamuel.kotest.matchers.reflection

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.compose.all
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

         val matcherResult = Matcher.all(
            nameMatcher to Person::name,
            ageMatcher to Person::age,
            addressMatcher to Person::address
         ).test(Person("John", 10, Address("Warsaw", "Test", "1/1")))

         matcherResult.passed() shouldBe true
         matcherResult.failureMessage() shouldBe ""
         matcherResult.negatedFailureMessage() shouldBe """
            Name John should not be John
            Age 10 should not be 10
            Address ${Address("Warsaw", "Test", "1/1")} should not be Test 1/1 Warsaw
         """.trimIndent()
      }

      "should filter out successful matchers when using properties" {

         val matcherResult = Matcher.all(
            nameMatcher to Person::name,
            ageMatcher to Person::age,
            addressMatcher to Person::address
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

      "should filter out successful matchers when using matchers" {

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
   }
}
