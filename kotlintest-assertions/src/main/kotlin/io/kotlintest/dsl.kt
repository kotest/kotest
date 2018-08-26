package io.kotlintest

import io.kotlintest.matchers.ToleranceMatcher

/**
 * Run multiple assertions, collecting any failures into a single exception that is thrown at the end of the
 * block.
 *
 * ```
 * verifyAll {
 *   foo shouldBe bar
 *   baz.shouldBeLessThan(qux)
 * }
 * ```
 */
inline fun <T> assertSoftly(assertions: () -> T): T {
  // Handle the edge case of nested calls to this function by only calling throwCollectedErrors in the
  // outermost verifyAll block
  if (ErrorCollector.shouldCollectErrors.get()) return assertions()
  ErrorCollector.shouldCollectErrors.set(true)
  return assertions().apply {
    ErrorCollector.throwCollectedErrors()
  }
}

fun <T> be(expected: T) = equalityMatcher(expected)
fun <T> equalityMatcher(expected: T) = object : Matcher<T> {
  override fun test(value: T): Result {
    val expectedRepr = stringRepr(expected)
    val valueRepr = stringRepr(value)
    return Result(expected == value, equalsErrorMessage(expectedRepr, valueRepr), "$expectedRepr should not equal $valueRepr")
  }
}

fun fail(msg: String): Nothing = throw Failures.failure(msg)

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
        ErrorCollector.collectOrThrow(equalsError(any, this))
      } else if (!compare(this, any)) {
        ErrorCollector.collectOrThrow(equalsError(any, this))
      }
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
    ErrorCollector.collectOrThrow(Failures.failure(result.failureMessage))
}

infix fun <T> T.shouldNotHave(matcher: Matcher<T>) = shouldNot(matcher)
infix fun <T> T.shouldNot(matcher: Matcher<T>) = should(matcher.invert())

infix fun <T> T.should(matcher: (T) -> Unit) = matcher(this)


// -- specialized overrides of shouldBe --

private fun equalsError(expected: Any?, actual: Any?): Throwable {
  val expectedRepr = stringRepr(expected)
  val actualRepr = stringRepr(actual)
  val message = equalsErrorMessage(expectedRepr, actualRepr)
  val throwable = junit5assertionFailedError(message, expectedRepr, actualRepr)
      ?: junit4comparisonFailure(expectedRepr, actualRepr)
      ?: AssertionError(message)
  if (Failures.shouldRemoveKotlintestElementsFromStacktrace) {
    Failures.removeKotlintestElementsFromStacktrace(throwable)
  }
  return throwable
}

private fun equalsErrorMessage(expected: Any?, actual: Any?) = "expected: $expected but was: $actual"

/** If JUnit5 is present, return an AssertionFailedError */
private fun junit5assertionFailedError(message: String, expected: Any?, actual: Any?): Throwable? {
  return callPublicConstructor("org.opentest4j.AssertionFailedError",
      arrayOf(String::class.java, Object::class.java, Object::class.java),
      arrayOf(message, expected, actual)) as? Throwable
}

/** If JUnit4 is present, return a ComparisonFailure */
private fun junit4comparisonFailure(expected: String, actual: String): Throwable? {
  return callPublicConstructor("org.junit.ComparisonFailure",
      arrayOf(String::class.java, String::class.java, String::class.java),
      arrayOf("", expected, actual)) as? Throwable
}

/**
 * Create an instance of the class named [className], with the [args] of type [parameterTypes]
 *
 * The constructor must be public.
 *
 * @return The constructed object, or null if any error occurred.
 */
private fun callPublicConstructor(className: String, parameterTypes: Array<Class<*>>, args: Array<Any?>): Any? {
  return try {
    val targetType = Class.forName(className)
    val constructor = targetType.getConstructor(*parameterTypes)
    constructor.newInstance(*args)
  } catch (t: Throwable) {
    null
  }
}

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
  return if (root == node) "(this ${root::class.java.simpleName})" else stringRepr(node)
}

// -- deprecated dsl

@Deprecated("shouldEqual is deprecated in favour of shouldBe", ReplaceWith("shouldBe(any)"))
infix fun <T> T.shouldEqual(any: Any?) = shouldBe(any)
