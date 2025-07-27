package io.kotest.core

enum class Platform {
   JVM, JS, Native, WasmJs, WasmWasi
}

expect val platform: Platform
