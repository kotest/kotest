package io.kotlintest

import com.github.difflib.DiffUtils
import com.github.difflib.patch.Chunk
import com.github.difflib.patch.Delta
import com.github.difflib.patch.DeltaType
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
  if (AssertionErrorCollector.shouldCollectErrors.get()) return assertions()
  AssertionErrorCollector.shouldCollectErrors.set(true)
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
        AssertionErrorCollector.collectOrThrow(equalsError(any, this))
      } else if (!compare(this, any)) {
        AssertionErrorCollector.collectOrThrow(equalsError(any, this))
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
actual infix fun <T> T.should(matcher: Matcher<T>) {
  val result = matcher.test(this)
  if (!result.passed) {
    AssertionErrorCollector.collectOrThrow(Failures.failure(AssertionErrorCollector.getClueContext() + result.failureMessage))
  }
}

infix fun <T> T.shouldNotHave(matcher: Matcher<T>) = shouldNot(matcher)
actual infix fun <T> T.shouldNot(matcher: Matcher<T>) = should(matcher.invert())

infix fun <T> T.should(matcher: (T) -> Unit) = matcher(this)


// -- specialized overrides of shouldBe --

internal fun equalsError(expected: Any?, actual: Any?): Throwable {

  val (expectedRepr, actualRepr) = diffLargeString(stringRepr(expected), stringRepr(actual))
  val message = AssertionErrorCollector.getClueContext() + equalsErrorMessage(expectedRepr, actualRepr)

  val throwable = junit5AssertionFailedError(message, expectedRepr, actualRepr)
      ?: junit4comparisonFailure(expectedRepr, actualRepr)
      ?: AssertionError(message)

  if (Failures.shouldRemoveKotlintestElementsFromStacktrace) {
    Failures.removeKotlintestElementsFromStacktrace(throwable)
  }
  return throwable
}

/**
 * Returns a formatted diff of the expected and actual input, unless there are no differences,
 * or the input is too small to bother with diffing, return it returns the input as is.
 */
@Suppress("MoveLambdaOutsideParentheses")
fun diffLargeString(expected: String, actual: String, minSizeForDiff: Int = 50): Pair<String, String> {

  fun typeString(deltaType: DeltaType): String = when (deltaType) {
    DeltaType.CHANGE -> "Change"
    DeltaType.INSERT -> "Addition"
    DeltaType.DELETE -> "Deletion"
    DeltaType.EQUAL -> ""
  }

  fun diffs(lines: List<String>, deltas: List<Delta<String>>, chunker: (Delta<String>) -> Chunk<String>): String {
    return deltas.joinToString("\n\n") { delta ->
      val chunk = chunker(delta)
      // include a line before and after to give some context on deletes
      val snippet = lines.drop(Math.max(chunk.position - 1, 0)).take(chunk.position + chunk.size()).joinToString("\n")
      "[${typeString(delta.type)} at line ${chunk.position}] $snippet"
    }
  }

  val useDiff = expected.lines().size >= minSizeForDiff
      && actual.lines().size >= minSizeForDiff &&
      System.getProperty("kotlintest.assertions.multi-line-diff") != "simple"

  return if (useDiff) {
    val patch = DiffUtils.diff(actual, expected)
    return if (patch.deltas.isEmpty()) Pair(expected, actual) else {
      Pair(diffs(expected.lines(), patch.deltas, { it.original }), diffs(actual.lines(), patch.deltas, { it.revised }))
    }
  } else Pair(expected, actual)
}

private fun equalsErrorMessage(expected: Any?, actual: Any?) = "expected: $expected but was: $actual"

/** If JUnit5 is present, return an org.opentest4j.AssertionFailedError */
private fun junit5AssertionFailedError(message: String, expected: Any?, actual: Any?): Throwable? {
  return callPublicConstructor("org.opentest4j.AssertionFailedError",
      arrayOf(String::class.java, Object::class.java, Object::class.java),
      arrayOf(message, expected, actual)) as? Throwable
}

/** If JUnit4 is present, return a org.junit.ComparisonFailure */
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
