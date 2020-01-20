package io.kotest.core.runtime

import io.kotest.core.test.TestCase

/**
 * This is invoked at compile time by the Javascript compiler to generate calls to kotest for each test case.
 * On other platforms this method is a no-op.
 */
expect fun executeJavascriptTests(rootTests: List<TestCase>)

expect fun configureRuntime()
