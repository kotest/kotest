package io.kotest.core

import io.kotest.core.spec.SpecConfiguration

/**
 * Returns the focused tests for this Spec. A focused test is one whose name begins with f:
 * Will return an empty list if no test is marked as focused.
 */
fun SpecConfiguration.focused(): List<TestCase> = rootTestCases.filter { it.name.startsWith("f:") }
