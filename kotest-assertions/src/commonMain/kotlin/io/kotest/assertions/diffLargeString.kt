package io.kotest.assertions

expect fun diffLargeString(expected: String, actual: String, minSizeForDiff: Int): Pair<String, String>
