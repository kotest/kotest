package io.kotlintest

import com.github.difflib.DiffUtils
import com.github.difflib.patch.Delta
import com.github.difflib.patch.DeltaType

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
  if (!result.passed) {
    ErrorCollector.collectOrThrow(Failures.failure(ErrorCollector.clueContext.get() + result.failureMessage))
  }
}

infix fun <T> T.shouldNotHave(matcher: Matcher<T>) = shouldNot(matcher)
infix fun <T> T.shouldNot(matcher: Matcher<T>) = should(matcher.invert())

infix fun <T> T.should(matcher: (T) -> Unit) = matcher(this)


// -- specialized overrides of shouldBe --

private fun equalsError(expected: Any?, actual: Any?): Throwable {

  val expectedRepr = stringRepr(expected)
  val actualRepr = stringRepr(actual)
  val message = ErrorCollector.clueContext.get() + equalsErrorMessage(expectedRepr, actualRepr)
  val throwable = junit5AssertionFailedError(message, expectedRepr, actualRepr)
      ?: junit4comparisonFailure(expectedRepr, actualRepr)
      ?: multiLineError(expectedRepr, actualRepr)
      ?: AssertionError(message)

  if (Failures.shouldRemoveKotlintestElementsFromStacktrace) {
    Failures.removeKotlintestElementsFromStacktrace(throwable)
  }
  return throwable
}

/**
 * Returns a diff-error of the lines that did not match and throws that as an [AssertionError].
 * The generated error message must be parsable by intellij, see:
 * https://github.com/JetBrains/intellij-community/blob/99614a63425774df58eea3ac92e2b20af1633663/plugins/junit_rt/src/com/intellij/junit4/ExpectedPatterns.java#L27
 * for regexes supported by intellij.
 */
fun multiLineError(expected: String, actual: String): Throwable? {

  fun typeString(deltaType: DeltaType): String = when (deltaType) {
    DeltaType.CHANGE -> "Change"
    DeltaType.INSERT -> "Addition"
    DeltaType.DELETE -> "Deletion"
    DeltaType.EQUAL -> ""
  }

  data class Change(val original: String, val originalLine: Int, val revised: String, val revisedLine: Int, val type: String)

  fun changes(delta: Delta<String>): Change {
    // include a line before and after to give some context on deletes
    val a = actual.lines().drop(Math.max(delta.original.position - 2, 0)).take(delta.original.size() + 1).joinToString("\n").trim()
    val b = expected.lines().drop(Math.max(delta.revised.position - 2, 0)).take(delta.revised.size() + 1).joinToString("\n").trim()
    return Change(a, delta.original.position, b, delta.revised.position, typeString(delta.type))
  }

  val patch = DiffUtils.diff(actual, expected)
  return if (patch.deltas.isEmpty()) null else {
    val expected2 = patch.deltas.map(::changes).joinToString("\n\n") { "[${it.type} at line ${it.originalLine}] ${it.original}" }
    val actual2 = patch.deltas.map(::changes).joinToString("\n\n") { "[${it.type} at line ${it.revisedLine}] ${it.revised}" }
    AssertionError("\nexpected: \"$expected2\"\n but: was \"$actual2\"")
  }
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

// -- deprecated dsl

@Deprecated("shouldEqual is deprecated in favour of shouldBe", ReplaceWith("shouldBe(any)"))
infix fun <T> T.shouldEqual(any: Any?) = shouldBe(any)
