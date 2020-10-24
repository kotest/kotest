package io.kotest.core.test

/**
 * Returns true if this test is a focused test.
 * That is, if the name starts with "f:" and is not nested
 */
fun TestCase.isFocused() = description.name.focus && description.parent.isSpec()

/**
 * Returns true if this test is disabled by being prefixed with a !
 */
fun TestCase.isBang(): Boolean = description.name.bang
