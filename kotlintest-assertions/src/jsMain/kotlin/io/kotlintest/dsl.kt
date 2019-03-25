package io.kotlintest


actual fun diffLargeString(expected: String, actual: String, minSizeForDiff: Int): Pair<String, String> {
  TODO()
}

internal actual fun createEqualsError(message: String, expectedRepr: String, actualRepr: String): Throwable {
  return AssertionError(message)
}
