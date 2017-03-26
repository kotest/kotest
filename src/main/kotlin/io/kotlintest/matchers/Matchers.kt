package io.kotlintest.matchers

import org.junit.ComparisonFailure

fun <T> equalityMatcher(expected: T) = object : Matcher<T> {
  override fun test(value: T): Result = Result(this == value, "$expected should equal $value")
}

fun fail(msg: String): Nothing = throw AssertionError(msg)

infix fun Double.shouldBe(other: Double): Unit = should(ToleranceMatcher(other, 0.0))

infix fun String.shouldBe(other: String) {
  if (this != other) {
    throw ComparisonFailure("", this, other)
  }
}

infix fun BooleanArray.shouldBe(other: BooleanArray): Unit {
  if (this.toList() != other.toList())
    throw AssertionError("Array not equal: $this != $other")
}

infix fun IntArray.shouldBe(other: IntArray): Unit {
  if (this.toList() != other.toList())
    throw AssertionError("Array not equal: $this != $other")
}

infix fun DoubleArray.shouldBe(other: DoubleArray): Unit {
  if (this.toList() != other.toList())
    throw AssertionError("Array not equal: $this != $other")
}

infix fun LongArray.shouldBe(other: LongArray): Unit {
  if (this.toList() != other.toList())
    throw AssertionError("Array not equal: $this != $other")
}

infix fun <T> Array<T>.shouldBe(other: Array<T>): Unit {
  if (this.toList() != other.toList())
    throw AssertionError("Array not equal: $this != $other")
}

infix fun <T> T.shouldHave(matcher: Matcher<T>) = should(matcher)
infix fun <T> T.shouldBe(any: Any?): Unit = shouldEqual(any)
infix fun <T> T.shouldEqual(any: Any?): Unit {
  when (any) {
    is Matcher<*> -> should(any as Matcher<T>)
    else -> {
      if (this == null && any != null)
        throw AssertionError(this.toString() + " did not equal $any")
      if (this != any)
        throw AssertionError(this.toString() + " did not equal $any")
    }
  }
}

infix fun <T> T.should(matcher: (T) -> Unit): Unit = matcher(this)

infix fun <T> T.should(matcher: Matcher<T>): Unit {
  val result = matcher.test(this)
  if (!result.passed)
    throw AssertionError(result.message)
}

infix fun <T> T.shouldNotBe(any: Any?): Unit {
  when (any) {
    is Matcher<*> -> shouldNot(any as Matcher<T>)
    else -> shouldNot(equalityMatcher(this))
  }
}

infix fun <T> T.shouldNot(matcher: Matcher<T>): Unit {
  val result = matcher.test(this)
  if (result.passed)
    throw AssertionError("Test passed which should have failed: " + result.message)
}