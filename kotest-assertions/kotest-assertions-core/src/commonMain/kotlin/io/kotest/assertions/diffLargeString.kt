package io.kotest.assertions

/**
 * Relies on a JVM only package, so on JS etc will return no diff.
 */
expect fun diffLargeString(expected: String, actual: String): Pair<String, String>?
