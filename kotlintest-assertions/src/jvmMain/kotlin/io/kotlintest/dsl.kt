@file:JvmName("dslJvm")
package io.kotlintest

import com.github.difflib.DiffUtils
import com.github.difflib.patch.Chunk
import com.github.difflib.patch.Delta
import com.github.difflib.patch.DeltaType

@Suppress("MoveLambdaOutsideParentheses")
actual fun diffLargeString(expected: String, actual: String, minSizeForDiff: Int): Pair<String, String> {

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

internal actual fun createEqualsError(message: String, expectedRepr: String, actualRepr: String): Throwable {
  return junit5AssertionFailedError(message, expectedRepr, actualRepr)
          ?: junit4comparisonFailure(expectedRepr, actualRepr)
          ?: AssertionError(message)
}

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
