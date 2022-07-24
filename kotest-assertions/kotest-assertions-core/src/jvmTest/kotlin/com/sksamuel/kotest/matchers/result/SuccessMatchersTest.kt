package com.sksamuel.kotest.matchers.result

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.result.SuccessMatcher
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.result.shouldNotBeSuccess
import io.kotest.matchers.shouldBe
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class SuccessMatchersTest : FunSpec({

  context("Matcher") {
    val matcher = SuccessMatcher("abc")

    test("Passes with equal success") {
      matcher.test(success("abc")).passed() shouldBe true
    }

    test("Passes when expected is Unit") {
      val matcher: Matcher<Result<*>> = SuccessMatcher(Unit)
      matcher.test(success("abc")).passed() shouldBe true
    }

    test("Fails with success with different content") {
      val badSuccess = success("cba")

      assertSoftly(matcher.test(badSuccess)) {
        passed() shouldBe false
        failureMessage() shouldBe """Result should be Success(abc), but was Success(cba)"""
        negatedFailureMessage() shouldBe """Result should not be a Success, but was Success(cba)"""
      }
    }

    test("Fails with a failure") {
      val failure = failure<String>(DummyException)

      assertSoftly(matcher.test(failure)) {
        passed() shouldBe false
        failureMessage() shouldBe """Expected to assert on a Success, but was Failure(DummyException)"""
      }
    }
  }

  context("Extensions") {
    test("Result.shouldBeSuccess") {
      success("abc").shouldBeSuccess()
      shouldThrowAny { failure<String>(DummyException).shouldBeSuccess() }
    }

    test("Result shouldBeSuccess with target") {
      success("abc") shouldBeSuccess "abc"
      shouldThrowAny { success("abc") shouldBeSuccess "cba" }
      shouldThrowAny { failure<String>(DummyException) shouldBeSuccess "abc" }
    }

    context("Result should be success with block") {
      test("Doesn't run block on failure") {
        shouldThrow<AssertionError> {
          failure<String>(DummyException) shouldBeSuccess {
            neverRunMe()
          }
        }
      }

      test("Invokes block exactly once on a success") {
        var timesBlockRan = 0
        val block: () -> Unit = { timesBlockRan++ }

        success("abc") shouldBeSuccess {
          block()
        }

        timesBlockRan shouldBe 1
      }

      test("Invokes block with success result") {
        var valueUsedInBlock: Any? = null
        val block: (Any) -> Unit = { valueUsedInBlock = it }

        success("abc") shouldBeSuccess {
          block(it)
        }

        valueUsedInBlock shouldBe "abc"
      }
    }

    test("Result.shouldNotBeSuccess") {
      failure<String>(DummyException).shouldNotBeSuccess()
      shouldThrowAny { success("abc").shouldNotBeSuccess() }
    }
  }

})

private fun neverRunMe(): Nothing = throw Error()

private object DummyException : Throwable() {
  override fun toString() = "DummyException"
}

