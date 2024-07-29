package com.sksamuel.kotest.matchers.compose

import io.kotest.assertions.shouldFailWithMessage
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.compose.any
import io.kotest.matchers.reflection.havingProperty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.contain
import io.kotest.matchers.string.containADigit

class ComposeAnyTest : StringSpec() {
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

      "Person Matcher.any test, all matchers pass" {

         val matcherResult = Matcher.any(
            havingProperty(nameMatcher to Person::name),
            havingProperty(ageMatcher to Person::age),
            havingProperty(addressMatcher to Person::address)
         ).test(Person("John", 10, Address("Warsaw", "Test", "1/1")))

         matcherResult.passed() shouldBe true
         matcherResult.failureMessage() shouldBe """
            None of composed matchers passed. Expecting at least one of them to pass:
            Name John should be John
            Age 10 should be 10
            Address Address(city=Warsaw, street=Test, buildingNumber=1/1) should be Test 1/1 Warsaw
         """.trimIndent()
         matcherResult.negatedFailureMessage() shouldBe """
            Name John should not be John
            Age 10 should not be 10
            Address ${Address("Warsaw", "Test", "1/1")} should not be Test 1/1 Warsaw
         """.trimIndent()
      }

      "Person Matcher.any test, all matchers fails" {

         val matcherResult = Matcher.any(
            havingProperty(nameMatcher to Person::name),
            havingProperty(ageMatcher to Person::age),
            havingProperty(addressMatcher to Person::address)
         ).test(Person("Jan", 27, Address("Berlin", "Test", "1/1")))

         matcherResult.passed() shouldBe false
         matcherResult.failureMessage() shouldBe """
            None of composed matchers passed. Expecting at least one of them to pass:
            Name Jan should be John
            Age 27 should be 10
            Address Address(city=Berlin, street=Test, buildingNumber=1/1) should be Test 1/1 Warsaw
         """.trimIndent()
         matcherResult.negatedFailureMessage() shouldBe """
            Name Jan should not be John
            Age 27 should not be 10
            Address ${Address("Berlin", "Test", "1/1")} should not be Test 1/1 Warsaw
         """.trimIndent()
      }

      "Person Matcher.any test, one matcher passes" {

         val matcherResult = Matcher.any(
            havingProperty(nameMatcher to Person::name),
            havingProperty(ageMatcher to Person::age),
            havingProperty(addressMatcher to Person::address)
         ).test(Person("Jan", 27, Address("Warsaw", "Test", "1/1")))

         matcherResult.passed() shouldBe true
         matcherResult.failureMessage() shouldBe """
            None of composed matchers passed. Expecting at least one of them to pass:
            Name Jan should be John
            Age 27 should be 10
            Address Address(city=Warsaw, street=Test, buildingNumber=1/1) should be Test 1/1 Warsaw
         """.trimIndent()
         matcherResult.negatedFailureMessage() shouldBe """
            Name Jan should not be John
            Age 27 should not be 10
            Address ${Address("Warsaw", "Test", "1/1")} should not be Test 1/1 Warsaw
         """.trimIndent()
      }

      "Person Matcher.any test, should fail on mismatching types" {
         val expectedErrorMessage =
            if (testJavaLauncherVersion >= 9) {
               "Mismatching type of matcher for property name: " +
                  "class java.lang.String cannot be cast to class java.lang.Number " +
                  "(java.lang.String and java.lang.Number are in module java.base of loader 'bootstrap')"
            } else {
               "Mismatching type of matcher for property name: " +
                  "java.lang.String cannot be cast to java.lang.Number"
            }

         shouldFailWithMessage(expectedErrorMessage) {
            Matcher.any(
               havingProperty(ageMatcher to Person::name),
               havingProperty(ageMatcher to Person::age),
               havingProperty(addressMatcher to Person::address)
            ).test(Person("John", 10, Address("Warsaw", "Test", "1/1")))
         }
      }

      "password Matcher.any test, should pass if one matcher passes" {

         val passwordMatcher = Matcher.any(
            containADigit(), contain(Regex("[a-z]")), contain(Regex("[A-Z]"))
         )

         val matcherResult = passwordMatcher.test("1")

         matcherResult.passed() shouldBe true
         matcherResult.failureMessage() shouldBe """
            None of composed matchers passed. Expecting at least one of them to pass:
            "1" should contain at least one digit
            "1" should contain regex [a-z]
            "1" should contain regex [A-Z]
         """.trimIndent()
         matcherResult.negatedFailureMessage() shouldBe """
            "1" should not contain any digits
            "1" should not contain regex [a-z]
            "1" should not contain regex [A-Z]
         """.trimIndent()
      }

      "password Matcher.any should fail if none matcher passes" {

         val passwordMatcher = Matcher.any(
            containADigit(), contain(Regex("[a-z]")), contain(Regex("[A-Z]"))
         )

         val matcherResult = passwordMatcher.test("")

         matcherResult.passed() shouldBe false
         matcherResult.failureMessage() shouldBe """
            None of composed matchers passed. Expecting at least one of them to pass:
            <empty string> should contain at least one digit
            <empty string> should contain regex [a-z]
            <empty string> should contain regex [A-Z]
         """.trimIndent()
         matcherResult.negatedFailureMessage() shouldBe """
            <empty string> should not contain any digits
            <empty string> should not contain regex [a-z]
            <empty string> should not contain regex [A-Z]
         """.trimIndent()
      }
   }

   companion object {
      /**
       * The version of Java used to run the tests.
       *
       * It must be passed in as a system property, in the Gradle Test task config.
       */
      val testJavaLauncherVersion = System.getProperty("testJavaLauncherVersion")?.toInt()
         ?: error("Missing 'testJavaLauncherVersion' system property")
   }
}
