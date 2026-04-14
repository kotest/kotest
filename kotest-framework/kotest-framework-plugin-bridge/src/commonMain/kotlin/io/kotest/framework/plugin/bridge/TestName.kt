package io.kotest.framework.plugin.bridge

/**
 * Normalizes a test name by trimming leading/trailing whitespace and collapsing
 * any internal whitespace sequences (including newlines and tabs) into a single space.
 */
fun String.normalizeTestName(): String = trim().replace(Regex("\\s+"), " ")
