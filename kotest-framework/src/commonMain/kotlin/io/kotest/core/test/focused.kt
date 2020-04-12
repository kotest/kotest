package io.kotest.core.test

/**
 * Returns true if this test is a focused test.
 * That is, if the name starts with "f:".
 */
fun TestCase.isFocused() = name.startsWith("f:")

/**
 * Returns true if this test is disabled by being prefixed with a !
 */
fun TestCase.isBang(): Boolean = name.startsWith("!")
