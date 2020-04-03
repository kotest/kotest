package com.sksamuel.kotest

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.failure
import io.kotest.assertions.show.Printed
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeInstanceOf
import org.opentest4j.AssertionFailedError

class FailuresTest : StringSpec({

   "failure(msg) should create a AssertionError on the JVM" {
      val t = failure("msg")
      t.shouldBeInstanceOf<AssertionError>()
      t.message shouldBe "msg"
   }

   "failure(msg, cause) should create a AssertionError with the given cause on the JVM" {
      val cause = RuntimeException()
      val t = failure("msg", cause)
      t.shouldBeInstanceOf<AssertionError>()
      t.message shouldBe "msg"
      t.cause shouldBe cause
   }

   "failure(expected, actual) should create a org.opentest4j.AssertionFailedError with JVM" {
      val expected = Expected(Printed("1"))
      val actual = Actual(Printed("2"))
      val t = failure(expected, actual)
      t.shouldBeInstanceOf<AssertionFailedError>()
      t.message shouldBe "expected:<1> but was:<2>"
   }

   "failure(msg) should filter the stack trace removing io.kotest" {
      val failure = failure("msg")
      failure.stackTrace[0].className.shouldStartWith("com.sksamuel.kotest.FailuresTest")
   }

   "failure(msg, cause) should filter the stack trace removing io.kotest" {
      val cause = RuntimeException()
      val t = failure("msg", cause)
      t.cause shouldBe cause
      t.stackTrace[0].className.shouldStartWith("com.sksamuel.kotest.FailuresTest")
   }

   "failure(expected, actual) should filter the stack trace removing io.kotest" {
      val expected = Expected(Printed("1"))
      val actual = Actual(Printed("2"))
      val t = failure(expected, actual)
      t.stackTrace[0].className.shouldStartWith("com.sksamuel.kotest.FailuresTest")
   }

   "filters stacktrace when called by shouldBe" {
      val t = shouldThrowAny { 1 shouldBe 2 }
      t.stackTrace[0].className.shouldStartWith("com.sksamuel.kotest.FailuresTest")
   }
})
