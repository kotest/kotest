package io.kotlintest

/**
 * Meta information about a [TestCase].
 */
data class TestCaseContext(
    val spec: TestBase,
    val testCase: TestCase)