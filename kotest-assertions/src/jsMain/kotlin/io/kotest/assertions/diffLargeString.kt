package io.kotest.assertions

actual fun diffLargeString(expected: String, actual: String, minSizeForDiff: Int) = Pair(expected, actual)
