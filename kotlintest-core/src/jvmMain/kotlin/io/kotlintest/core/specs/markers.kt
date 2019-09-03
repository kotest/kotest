package io.kotlintest.core.specs

import io.kotlintest.TestCase

actual typealias Junit5TestFactory = org.junit.jupiter.api.TestFactory

actual typealias Junit5EnabledIfSystemProperty = org.junit.jupiter.api.condition.EnabledIfSystemProperty

actual typealias Testable = org.junit.platform.commons.annotation.Testable

actual annotation class JsTest

// on the JVM this will do nothing as the tests will be picked up by junit platform discovery
actual fun generateTests(rootTests: List<TestCase>) {}

actual typealias AutoCloseable = java.lang.AutoCloseable
