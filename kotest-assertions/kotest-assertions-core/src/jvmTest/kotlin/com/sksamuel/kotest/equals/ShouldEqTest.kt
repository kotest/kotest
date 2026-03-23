package com.sksamuel.kotest.equals

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldEq
import io.kotest.matchers.equals.shouldNotEq
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class ShouldEqTest : FunSpec() {
   init {

      test("equal values should pass") {
         1 shouldEq 1
         "hello" shouldEq "hello"
         null shouldEq null
      }

      test("unequal values should fail") {
         shouldThrow<AssertionError> {
            1 shouldEq 2
         }
      }

      test("shouldNotEq should pass for unequal values") {
         1 shouldNotEq 2
         "hello" shouldNotEq "world"
      }

      test("shouldNotEq should fail for equal values") {
         shouldThrow<AssertionError> {
            1 shouldNotEq 1
         }
      }

      test("shouldEq should return the receiver for chaining") {
         val result = 1 shouldEq 1
         result shouldBe 1
      }

      test("shouldNotEq should return the receiver for chaining") {
         val result = 1 shouldNotEq 2
         result shouldBe 1
      }

      test("shouldEq should work with custom equals") {
         class Wrapper(val value: Int) {
            override fun equals(other: Any?): Boolean {
               return other is Wrapper && other.value == value
            }

            override fun hashCode(): Int = value
         }
         Wrapper(1) shouldEq Wrapper(1)
         Wrapper(1) shouldNotEq Wrapper(2)
      }

      test("shouldEq should work with nullable types") {
         val a: String? = null
         val b: String? = null
         a shouldEq b
      }

      test("shouldEq should work with subtypes through common receiver") {
         val list: List<Int> = listOf(1, 2, 3)
         val other: List<Int> = listOf(1, 2, 3)
         list shouldEq other
      }

      test("shouldEq should generate a meaningful error message") {
         shouldThrow<AssertionError> {
            "foo" shouldEq "bar"
         }.message shouldBe """foo should be equal to bar
expected:<"bar"> but was:<"foo">"""
      }
   }
}
