package io.kotlintest

import io.kotlintest.matchers.AssertionErrorCollector

/**
 * Run multiple assertions and throw a single error after all are executed if any fail
 *
 * This method will run all the assertions inside [assertions] block, and will collect all failures that may happen.
 * It then compact all of them in a single throwable and throw it instead, or nothing if no assertion fail.
 *
 * ```
 *     // All assertions below are going to be executed, even when one or multiple fail.
 *     // All the failures are then collected and thrown in one single throwable.
 *     assertSoftly {
 *         "foo" shouldBe "bar"
 *         "foo" shouldBe "foo
 *         "foo" shouldBe "baz"
 *     }
 * ```
 */
inline fun <T> assertSoftly(assertions: () -> T): T {
  // Handle the edge case of nested calls to this function by only calling throwCollectedErrors in the
  // outermost verifyAll block
  if (AssertionErrorCollector.shouldCollectErrors) return assertions()
  AssertionErrorCollector.shouldCollectErrors = true
  return assertions().apply {
    AssertionErrorCollector.throwCollectedErrors()
  }
}

fun <T> be(expected: T) = equalityMatcher(expected)
fun <T> equalityMatcher(expected: T) = object : Matcher<T> {
  override fun test(value: T): Result {
    val expectedRepr = stringRepr(expected)
    val valueRepr = stringRepr(value)
    return Result(expected == value,
            equalsErrorMessage(expectedRepr, valueRepr),
            "$expectedRepr should not equal $valueRepr")
  }
}

fun fail(msg: String): Nothing = throw Failures.failure(msg)

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
    else -> makeComparable(a) == makeComparable(b)
  }
}

private fun makeComparable(any: Any?): Any? {
  return when (any) {
    is BooleanArray -> any.asList()
    is IntArray -> any.asList()
    is ShortArray -> any.asList()
    is FloatArray -> any.asList()
    is DoubleArray -> any.asList()
    is LongArray -> any.asList()
    is ByteArray -> any.asList()
    is CharArray -> any.asList()
    is Array<*> -> any.asList()
    else -> any
  }
}

@Suppress("UNCHECKED_CAST")
infix fun <T, U : T> T.shouldBe(any: U?) {
  when (any) {
    is Matcher<*> -> should(any as Matcher<T>)
    else -> {
      if (this == null && any != null) {
        AssertionErrorCollector.collectOrThrow(equalsError(any, this))
      } else if (!compare(this, any)) {
        AssertionErrorCollector.collectOrThrow(equalsError(any, this))
      }
    }
  }
}

internal fun equalsError(expected: Any?, actual: Any?): Throwable {
  
  val (expectedRepr, actualRepr) = diffLargeString(stringRepr(expected), stringRepr(actual))
  val message = AssertionErrorCollector.getClueContext() + equalsErrorMessage(expectedRepr, actualRepr)
  
  val throwable = createEqualsError(message,  expectedRepr, actualRepr)
  
  return Failures.modifyThrowable(throwable)
}

internal expect fun createEqualsError(message: String, expectedRepr: String, actualRepr: String): Throwable


@Suppress("UNCHECKED_CAST")
infix fun <T> T.shouldNotBe(any: Any?) {
  when (any) {
    is Matcher<*> -> shouldNot(any as Matcher<T>)
    else -> shouldNot(equalityMatcher(any))
  }
}

infix fun <T> T.shouldHave(matcher: Matcher<T>) = should(matcher)
infix fun <T> T.should(matcher: Matcher<T>) {
  val result = matcher.test(this)
  if (!result.passed) {
    AssertionErrorCollector.collectOrThrow(Failures.failure(AssertionErrorCollector.getClueContext() + result.failureMessage))
  }
}

infix fun <T> T.shouldNotHave(matcher: Matcher<T>) = shouldNot(matcher)
infix fun <T> T.shouldNot(matcher: Matcher<T>) = should(matcher.invert())

infix fun <T> T.should(matcher: (T) -> Unit) = matcher(this)


/**
 * Returns a formatted diff of the expected and actual input, unless there are no differences,
 * or the input is too small to bother with diffing, return it returns the input as is.
 */
expect fun diffLargeString(expected: String, actual: String, minSizeForDiff: Int = 50): Pair<String, String>

private fun equalsErrorMessage(expected: Any?, actual: Any?) = "expected: $expected but was: $actual"

/** Return a string representation of [obj] that is less ambiguous than `toString` */
internal fun stringRepr(obj: Any?): String = when (obj) {
  is Float -> "${obj}f"
  is Long -> "${obj}L"
  is Char -> "'$obj'"
  is String -> "\"$obj\""
  is Array<*> -> obj.map { recursiveRepr(obj, it) }.toString()
  is FloatArray -> obj.map { recursiveRepr(obj, it) }.toString()
  is LongArray -> obj.map { recursiveRepr(obj, it) }.toString()
  is CharArray -> obj.map { recursiveRepr(obj, it) }.toString()
  is Iterable<*> -> obj.map { recursiveRepr(obj, it) }.toString()
  is Map<*, *> -> obj.map { (k, v) -> recursiveRepr(obj, k) to recursiveRepr(obj, v) }.toMap().toString()
  else -> obj.toString()
}

private fun recursiveRepr(root: Any, node: Any?): String {
  return if (root == node) "(this ${root::class.simpleName})" else stringRepr(node)
}

internal fun convertValueToString(value: Any?): String = when (value) {
  null -> "<null>"
  "" -> "<empty string>"
  else -> {
    val str = value.toString()
    if (str.isBlank()) str.replace("\n", "\\n").replace("\t", "\\t").replace(" ", "\\s") else str
  }
}