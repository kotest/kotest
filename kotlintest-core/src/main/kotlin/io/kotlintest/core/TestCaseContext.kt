package io.kotlintest.core

/**
 * Meta information about a [TestCase].
 */
data class TestCaseContext(
    val spec: AbstractSpec,
    val testCase: TestCase
)