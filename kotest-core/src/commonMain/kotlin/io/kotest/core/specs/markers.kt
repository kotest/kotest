package io.kotest.core.specs

// used by intellij to detect junit 5 tests
expect annotation class Junit5EnabledIfSystemProperty constructor(val named: String, val matches: String)
expect annotation class Junit5TestFactory()

// used by the kotlin compiler to generate test methods, we use this for js impl
expect annotation class JsTest()
