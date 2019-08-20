package io.kotlintest.core

import io.kotlintest.TestCase

actual typealias Junit5TestFactory = org.junit.jupiter.api.TestFactory

actual typealias Junit5EnabledIfSystemProperty = org.junit.jupiter.api.condition.EnabledIfSystemProperty

actual annotation class JsTest

// on the JVM this will do nothing as the tests will be picked up by junit platform discovery
actual fun generateTests(rootTests: List<TestCase>) {}
