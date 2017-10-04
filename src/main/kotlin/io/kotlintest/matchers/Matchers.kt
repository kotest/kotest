package io.kotlintest.matchers

import org.junit.ComparisonFailure

fun <T> equalityMatcher(expected: T) = object : Matcher<T> {
  override fun test(value: T): Result = Result(expected == value, equalsErrorMessage(expected, value))
}

fun fail(msg: String): Nothing = throw AssertionError(msg)

infix fun Double.shouldBe(other: Double) = should(ToleranceMatcher(other, 0.0))

infix fun String.shouldBe(other: String) {
  if (this != other) {
    throw ComparisonFailure("", other, this)
  }
}

infix fun BooleanArray.shouldBe(other: BooleanArray) {
  val expected = other.toList()
  val actual = this.toList()
  if (actual != expected)
    throw equalsError(expected, actual)
}

infix fun IntArray.shouldBe(other: IntArray) {
  val expected = other.toList()
  val actual = this.toList()
  if (actual != expected)
    throw equalsError(expected, actual)
}

infix fun DoubleArray.shouldBe(other: DoubleArray) {
  val expected = other.toList()
  val actual = this.toList()
  if (actual != expected)
    throw equalsError(expected, actual)
}

infix fun LongArray.shouldBe(other: LongArray) {
  val expected = other.toList()
  val actual = this.toList()
  if (actual != expected)
    throw equalsError(expected, actual)
}

infix fun <T> Array<T>.shouldBe(other: Array<T>) {
  val expected = other.toList()
  val actual = this.toList()
  if (actual != expected)
    throw equalsError(expected, actual)
}

infix fun <T> T.shouldHave(matcher: Matcher<T>) = should(matcher)
infix fun <T> T.shouldBe(any: Any?): Unit = shouldEqual(any)
infix fun <T> T.shouldEqual(any: Any?): Unit {
  when (any) {
    is Matcher<*> -> should(any as Matcher<T>)
    else -> {
      if (this == null && any != null)
        throw equalsError(any, this)
      if (this != any)
        throw equalsError(any, this)
    }
  }
}

infix fun <T> T.should(matcher: (T) -> Unit) = matcher(this)

infix fun <T> T.should(matcher: Matcher<T>) {
  val result = matcher.test(this)
  if (!result.passed)
    throw AssertionError(result.message)
}

infix fun <T> T.shouldNotBe(any: Any?) {
  when (any) {
    is Matcher<*> -> shouldNot(any as Matcher<T>)
    else -> shouldNot(equalityMatcher(any))
  }
}

infix fun <T> T.shouldNot(matcher: Matcher<T>) {
  val result = matcher.test(this)
  if (result.passed)
    throw AssertionError("Test passed which should have failed: " + result.message)
}

private fun equalsError(expected: Any?, actual: Any?) = AssertionError(equalsErrorMessage(expected, actual))
private fun equalsErrorMessage(expected: Any?, actual: Any?) = "expected: $expected but was: $actual"