package io.kotest.core.spec

actual typealias JsTest = kotlin.test.Test

actual interface AutoCloseable {
   actual fun close()
}
