package io.kotest.core

enum class Platform {
   JVM, JS, Native, WasmJs
}

expect val platform: Platform
