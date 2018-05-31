package io.kotlintest

import io.kotlintest.matchers.ToleranceMatcher
import org.junit.ComparisonCompactor

fun <T> be(expected: T) = equalityMatcher(expected)
fun <T> equalityMatcher(expected: T) = object : Matcher<T> {
  override fun test(value: T): Result = Result(expected == value, equalsErrorMessage(expected, value), "$value should not equal $expected")
}

fun fail(msg: String): Nothing = throw AssertionError(msg)

// -- equality functions

private fun compare(a: Any?, b: Any?): Boolean {
  return when (a) {
    is Int -> when (b) {
      is Long -> a.toLong() == b
      is Double -> a.toDouble() == b
      else -> a == b
    }
    is Float -> when (b) {
      is Double -> a.toDouble() == b
      else -> a == b
    }
    is Double -> when (b) {
      is Float -> a == b.toDouble()
      else -> a == b
    }
    is Long -> when (b) {
      is Int -> a == b.toLong()
      else -> a == b
    }
    else -> a == b
  }
}

@Suppress("UNCHECKED_CAST")
infix fun <T, U : T> T.shouldBe(any: U?) {
  when (any) {
    is Matcher<*> -> should(any as Matcher<T>)
    else -> {
      if (this == null && any != null)
        throw equalsError(any, this)
      if (!compare(this, any))
        throw equalsError(any, this)
    }
  }
}

@Suppress("UNCHECKED_CAST")
infix fun <T> T.shouldNotBe(any: Any?) {
  when (any) {
    is Matcher<*> -> shouldNot(any as Matcher<T>)
    else -> shouldNot(equalityMatcher(any))
  }
}

// -- matcher functions

infix fun <T> T.shouldHave(matcher: Matcher<T>) = should(matcher)
infix fun <T> T.should(matcher: Matcher<T>) {
  val result = matcher.test(this)
  if (!result.passed)
    throw AssertionError(result.failureMessage)
}

infix fun <T> T.shouldNotHave(matcher: Matcher<T>) = shouldNot(matcher)
infix fun <T> T.shouldNot(matcher: Matcher<T>) = should(matcher.invert())

infix fun <T> T.should(matcher: (T) -> Unit) = matcher(this)


// -- specialized overrides of shouldBe --

infix fun Double.shouldBe(other: Double) = should(ToleranceMatcher(other, 0.0))

// https://stackoverflow.com/questions/10934743/formatting-output-so-that-intellij-idea-shows-diffs-for-two-texts
// https://github.com/JetBrains/intellij-community/blob/3f7e93e20b7e79ba389adf593b3b59e46a3e01d1/plugins/testng/src/com/theoryinpractice/testng/model/TestProxy.java#L50
infix fun String.shouldBe(other: String) {
  if (this != other) {
    throw AssertionError(ComparisonCompactor.getMessage(other, this))
  }
}

infix fun BooleanArray?.shouldBe(other: BooleanArray?) {
  val expected = other?.asList()
  val actual = this?.asList()
  if (actual != expected)
    throw equalsError(expected, actual)
}

infix fun IntArray?.shouldBe(other: IntArray?) {
  val expected = other?.asList()
  val actual = this?.asList()
  if (actual != expected)
    throw equalsError(expected, actual)
}

infix fun ShortArray?.shouldBe(other: ShortArray?) {
  val expected = other?.asList()
  val actual = this?.asList()
  if (actual != expected)
    throw equalsError(expected, actual)
}

infix fun FloatArray?.shouldBe(other: FloatArray?) {
  val expected = other?.asList()
  val actual = this?.asList()
  if (actual != expected)
    throw equalsError(expected, actual)
}

infix fun DoubleArray?.shouldBe(other: DoubleArray?) {
  val expected = other?.asList()
  val actual = this?.asList()
  if (actual != expected)
    throw equalsError(expected, actual)
}

infix fun LongArray?.shouldBe(other: LongArray?) {
  val expected = other?.asList()
  val actual = this?.asList()
  if (actual != expected)
    throw equalsError(expected, actual)
}

infix fun ByteArray?.shouldBe(other: ByteArray?) {
  val expected = other?.asList()
  val actual = this?.asList()
  if (actual != expected)
    throw equalsError(expected, actual)
}

infix fun CharArray?.shouldBe(other: CharArray?) {
  val expected = other?.asList()
  val actual = this?.asList()
  if (actual != expected)
    throw equalsError(expected, actual)
}

infix fun <T> Array<T>?.shouldBe(other: Array<T>?) {
  val expected = other?.asList()
  val actual = this?.asList()
  if (actual != expected)
    throw equalsError(expected, actual)
}

private fun equalsError(expected: Any?, actual: Any?) = AssertionError(equalsErrorMessage(expected, actual))
private fun equalsErrorMessage(expected: Any?, actual: Any?) = "expected: $expected but was: $actual"

// -- deprecated dsl

@Deprecated("shouldEqual is deprecated in favour of shouldBe", ReplaceWith("shouldBe(any)"))
infix fun <T> T.shouldEqual(any: Any?) = shouldBe(any)
