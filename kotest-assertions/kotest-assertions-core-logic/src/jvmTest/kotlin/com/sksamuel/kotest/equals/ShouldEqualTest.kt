package com.sksamuel.kotest.equals

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldEqual
import io.kotest.matchers.equals.shouldNotEqual
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class ShouldEqualTest : FunSpec() {
   init {

      test("equal values should pass") {
         1 shouldEqual 1
         "hello" shouldEqual "hello"
         null shouldEqual null
      }

      test("unequal values should fail") {
         shouldThrow<AssertionError> {
            1 shouldEqual 2
         }
      }

      test("shouldNotEqual should pass for unequal values") {
         1 shouldNotEqual 2
         "hello" shouldNotEqual "world"
      }

      test("shouldNotEqual should fail for equal values") {
         shouldThrow<AssertionError> {
            1 shouldNotEqual 1
         }
      }

      test("shouldEqual should return the receiver for chaining") {
         val result = 1 shouldEqual 1
         result shouldBe 1
      }

      test("shouldNotEqual should return the receiver for chaining") {
         val result = 1 shouldNotEqual 2
         result shouldBe 1
      }

      test("shouldEqual should work with custom equals") {
         class Wrapper(val value: Int) {
            override fun equals(other: Any?): Boolean {
               return other is Wrapper && other.value == value
            }

            override fun hashCode(): Int = value
         }
         Wrapper(1) shouldEqual Wrapper(1)
         Wrapper(1) shouldNotEqual Wrapper(2)
      }

      test("shouldEqual should work with nullable types") {
         val a: String? = null
         val b: String? = null
         a shouldEqual b
      }

      test("shouldNotEqual should work with nullable types") {
         val a: String? = null
         val b: String? = "hello"
         a shouldNotEqual b
      }

      test("shouldEqual should work with subtypes through common receiver") {
         val list: List<Int> = listOf(1, 2, 3)
         val other: List<Int> = listOf(1, 2, 3)
         list shouldEqual other
      }

      test("shouldEqual should generate a meaningful error message") {
         shouldThrow<AssertionError> {
            "foo" shouldEqual "bar"
         }.message shouldBe """foo should be equal to bar
expected:<"bar"> but was:<"foo">"""
      }

      test("shouldNotEqual should generate a meaningful error message") {
         shouldThrow<AssertionError> {
            "foo" shouldNotEqual "foo"
         }.message shouldBe """foo should not be equal to foo"""
      }
   }
}
