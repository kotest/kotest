package com.sksamuel.kotest.matchers.result

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.result.*
import io.kotest.matchers.shouldBe
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success

class FailureMatchersTest : FunSpec({
  context("Matcher") {
    val matcher = FailureMatcher(DummyException2)

    test("Passes on same failure") {
      matcher.test(failure<String>(DummyException2)).passed() shouldBe true
    }

    test("Passes on any failure if expected type is AnyError") {
      val matcher = FailureMatcher(AnyError)
      matcher.test(failure<String>(DummyException2)).passed() shouldBe true
    }

    test("Passes on any failure if expected is left blank") {
      val matcher = FailureMatcher()
      matcher.test(failure<String>(DummyException2)).passed() shouldBe true
    }

    test("Fails on success") {
      assertSoftly(matcher.test(success("abc"))) {
        passed() shouldBe false
        failureMessage() shouldBe "Expected a Failure, but got Success(abc)"
      }
    }

    test("Fails on different exception") {
      assertSoftly(matcher.test(failure<String>(DummyException3))) {
        passed() shouldBe false
        failureMessage() shouldBe "Result should be Failure(DummyException2) but was Failure(DummyException3)"
      }
    }
  }

  context("Type matcher") {
    val matcher = FailureTypeMatcher(DummyException2::class)

    test("Fails on success") {
      assertSoftly(matcher.test(success("abc"))) {
        passed() shouldBe false
        failureMessage() shouldBe "Expected a Failure, but got Success(abc)"
      }
    }

    test("Fails on different type") {
      assertSoftly(matcher.test(failure<String>(DummyException3))) {
        passed() shouldBe false
        failureMessage() shouldBe "Result should be a failure of type ${DummyException2::class} but was ${DummyException3::class}"
      }
    }
  }

  context("Extensions") {
    test("Should not be failure") {
      success("abc").shouldNotBeFailure()
      shouldThrowAny { failure<String>(DummyException2).shouldNotBeFailure() }
    }

    test("Should be failure") {
      failure<String>(DummyException2).shouldBeFailure()
      shouldThrowAny { success("abc").shouldBeFailure() }
    }

    test("Should be failure with target") {
      failure<String>(DummyException2) shouldBeFailure DummyException2
      shouldThrowAny { failure<String>(DummyException2) shouldBeFailure DummyException3 }
      shouldThrowAny { success("ABC") shouldBeFailure DummyException2 }
    }

    context("Should be failure with target block") {
      test("Doesn't call block on success") {
        var callsToBlock = 0
        val block: () -> Unit = { callsToBlock++ }

        shouldThrowAny {
          success("abc") shouldBeFailure {
            block()
          }
        }
        callsToBlock shouldBe 0
      }

      test("Calls block exactly on failure once") {
        var callsToBlock = 0
        val block: () -> Unit = { callsToBlock++ }

        failure<String>(DummyException2) shouldBeFailure {
          block()
        }

        callsToBlock shouldBe 1
      }

      test("Calls block with throwable from Result") {
        var throwableInBlock: Throwable? = null
        val block: (Throwable) -> Unit = { throwableInBlock = it }

        failure<String>(DummyException2) shouldBeFailure {
          block(it)
        }

        throwableInBlock shouldBe DummyException2
      }
    }

    test("Should be failure with reified type") {
      failure<String>(DummyException2).shouldBeFailure<DummyException2>()
      shouldThrowAny { failure<String>(DummyException2).shouldBeFailure<DummyException3>() }
      shouldThrowAny { success("abc").shouldBeFailure<DummyException2>() }
    }
  }
})

private object DummyException2 : Exception() {
  override fun toString() = "DummyException2"
}

private object DummyException3 : Exception() {
  override fun toString() = "DummyException3"
}
