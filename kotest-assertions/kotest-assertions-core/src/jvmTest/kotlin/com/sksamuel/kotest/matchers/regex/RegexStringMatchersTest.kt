package com.sksamuel.kotest.matchers.regex

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.regex.shouldMatch
import io.kotest.matchers.regex.shouldMatchAll
import io.kotest.matchers.regex.shouldMatchAny
import io.kotest.matchers.regex.shouldNotMatch
import io.kotest.matchers.regex.shouldNotMatchAll
import io.kotest.matchers.regex.shouldNotMatchAny
import io.kotest.matchers.shouldBe

class RegexStringMatchersTest : FunSpec() {
   init {
      test("Regex.shouldMatch(str)") {
         "aab".toRegex() shouldMatch "aab"
         "aab.".toRegex() shouldMatch "aabz"
         "aab.*".toRegex() shouldMatch "aabzzzsdfsdf"

         "aab".toRegex() shouldNotMatch "bbc"
         "aab*".toRegex() shouldNotMatch "bbac"

         shouldThrow<AssertionError> {
            "aab.*".toRegex() shouldMatch "aa"
         }.message shouldBe "Regex 'aab.*' should match aa"

         shouldThrow<AssertionError> {
            "aab.".toRegex() shouldNotMatch "aabc"
         }.message shouldBe "Regex 'aab.' should not match aabc"
      }

      test("Regex.shouldMatchAll(str)") {
         "aab.".toRegex().shouldMatchAll("aabc", "aabb")
         "aab.+".toRegex().shouldMatchAll("aabbbbbbb", "aabcc")

         "aab".toRegex().shouldNotMatchAll("aab", "aaaaaa")

         shouldThrow<AssertionError> {
            "aab.".toRegex().shouldMatchAll("aab", "aabb", "aabc")
         }.message shouldBe "Regex 'aab.' did not match aab"

         shouldThrow<AssertionError> {
            "aab.".toRegex().shouldNotMatchAll("aabb", "aabc")
         }.message shouldBe "Regex 'aab.' should not match aabb, aabc"
      }

      test("Regex.shouldMatchAny(str)") {
         "aab.".toRegex().shouldMatchAny("aabc", "zzzzz")
         "aab.*".toRegex().shouldMatchAny("aabbbbbbb", "dfsdfsfsdfdf")

         "aab".toRegex().shouldNotMatchAny("wqeqwe", "wwrwerwe")

         shouldThrow<AssertionError> {
            "aab.*".toRegex().shouldMatchAny("ddd", "fff")
         }.message shouldBe "Regex 'aab.*' did not match any of ddd, fff"

         shouldThrow<AssertionError> {
            "aab.".toRegex().shouldNotMatchAny("aabb", "aabc", "fff")
         }.message shouldBe "Regex 'aab.' should not match aabb, aabc"
      }
   }
}
