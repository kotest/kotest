package io.kotlintest.core

actual annotation class Junit5TestFactory

actual annotation class Junit5EnabledIfSystemProperty constructor(actual val named: String, actual val matches: String)

actual typealias JsTest = kotlin.test.Test
