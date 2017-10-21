package io.kotlintest

/**
 * Meta information about a [TestCase].
 */
data class TestCaseContext(
    val spec: Spec,
    val testCase: TestCaseDescriptor
)