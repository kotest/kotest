package com.sksamuel.kotest

import io.kotest.assertions.Actual
import io.kotest.assertions.Expected
import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.errorCollector
import io.kotest.assertions.failure
import io.kotest.assertions.print.Printed
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.matchers.types.shouldBeInstanceOf
import org.opentest4j.AssertionFailedError

@EnabledIf(LinuxOnlyGithubCondition::class)
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

   "When failing with failure(msg), errorCollector should filter the stack trace removing io.kotest" {
      val failure = shouldThrow<AssertionError> { errorCollector.collectOrThrow(failure("msg")) }
      failure.stackTrace[0].className.shouldStartWith("com.sksamuel.kotest.FailuresTest")
   }

   "When failing with failure(msg, cause), errorCollector should filter the stack trace removing io.kotest" {
      val cause = RuntimeException()
      val t = shouldThrow<AssertionError> {
         errorCollector.collectOrThrow(failure("msg", cause))
      }
      t.cause shouldBe cause
      t.stackTrace[0].className.shouldStartWith("com.sksamuel.kotest.FailuresTest")
   }

   "When failing with failure(expected, actual), errorCollector should filter the stack trace removing io.kotest" {
      val expected = Expected(Printed("1"))
      val actual = Actual(Printed("2"))
      val t = shouldThrow<AssertionError> {
         errorCollector.collectOrThrow(failure(expected, actual))
      }
      t.stackTrace[0].className.shouldStartWith("com.sksamuel.kotest.FailuresTest")
   }

   "filters stacktrace when called by shouldBe" {
      val t = shouldThrowAny { 1 shouldBe 2 }
      t.stackTrace[0].className.shouldStartWith("com.sksamuel.kotest.FailuresTest")
   }
})
