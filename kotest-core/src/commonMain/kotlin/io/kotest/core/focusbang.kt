package io.kotest.core

import io.kotest.core.spec.SpecConfiguration

/**
 * Returns the focused root tests for this Spec. A focused test is one whose
 * name begins with "f:".
 *
 * Returns an empty list if no test is marked as focused.
 */
fun SpecConfiguration.focusTests(): List<TestCase> = rootTestCases.filter { it.isFocused() }

/**
 * Returns true if this test is a focused test.
 * That is, if the name starts with "f:".
 */
fun TestCase.isFocused() = name.startsWith("f:")

/**
 * Returns true if this test is disabled by being prefixed with a !
 */
fun TestCase.isBang(): Boolean = name.startsWith("!")
