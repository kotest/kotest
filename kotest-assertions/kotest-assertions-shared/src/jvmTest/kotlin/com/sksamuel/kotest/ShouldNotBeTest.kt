package com.sksamuel.kotest

import io.kotest.assertions.shouldFail
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.math.BigInteger

@EnabledIf(LinuxOnlyGithubCondition::class)
class ShouldNotBeTest : FunSpec() {

   data class Foo(val a: String, val b: Boolean, val c: Int)

   init {
      test("incompatible types should not be equal") {
         "a" shouldNotBe 3
         "3" shouldNotBe 3
         3 shouldNotBe "3"
         "a" shouldNotBe true
         false shouldNotBe 4
         10.0 shouldNotBe BigInteger.TEN
      }

      test("equal types should fail") {
         shouldFail { "a" shouldNotBe "a" }.shouldHaveMessage(""""a" should not equal "a"""")
         shouldFail { 3 shouldNotBe 3 }.shouldHaveMessage("3 should not equal 3")
         shouldFail { false shouldNotBe false }.shouldHaveMessage("false should not equal false")
      }

      test("data classes that differ in fields should not be equal") {
         Foo("a", true, 1) shouldNotBe Foo("b", true, 1)
         Foo("a", true, 1) shouldNotBe Foo("a", false, 1)
         Foo("a", true, 1) shouldNotBe Foo("a", true, 2)
         Foo("a", true, 1) shouldNotBe Foo("a", false, 2)
         Foo("a", true, 1) shouldNotBe Foo("b", false, 2)
      }

      test("data classes that are equal should fail") {
         shouldFail {
            Foo("a", true, 1) shouldNotBe Foo("a", true, 1)
         }.shouldHaveMessage("""Foo(a=a, b=true, c=1) should not equal Foo(a=a, b=true, c=1)""")
      }
   }
}
