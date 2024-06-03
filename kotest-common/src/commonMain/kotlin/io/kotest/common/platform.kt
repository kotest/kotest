package io.kotest.common

enum class Platform {
   JVM, JS, Native, WasmJs
}

expect val platform: Platform
