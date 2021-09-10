package io.kotest.engine.spec

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.isFocused

/**
 * Returns the focused root tests for this Spec. A focused test is one whose
 * name begins with "f:".
 *
 * Returns an empty list if no test is marked as focused.
 */
internal fun Spec.focusTests(): List<TestCase> = materializeRootTests().map { it.testCase }.filter { it.isFocused() }
