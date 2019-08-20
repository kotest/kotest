package io.kotlintest.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

actual typealias Junit5TestFactory = org.junit.jupiter.api.TestFactory

actual typealias Junit5EnabledIfSystemProperty = org.junit.jupiter.api.condition.EnabledIfSystemProperty

actual annotation class JsTest

actual fun container(name: String, fn: suspend TestContext.() -> Unit) {}

actual fun runTest(block: suspend (scope: CoroutineScope) -> Unit) = runBlocking { block(this) }
