package io.kotest.common

enum class Platform {
   JVM, JS, Native, WasmJs, WasmWasi
}

expect val platform: Platform
