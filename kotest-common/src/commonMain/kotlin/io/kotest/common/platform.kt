package io.kotest.common

enum class Platform {
   JVM, JS, Native, WasmJs, WasmWasi
}

/**
 * Returns the platform enum we are currently executing in.
 */
@Deprecated("Use platformExecution.platform")
val platform: Platform = platformExecution.platform

/**
 * Returns the platform environment we are currently executing in.
 */
expect val platformExecution: PlatformEnvironment

/**
 * Defines execution specifics for a given platform.
 */
interface PlatformEnvironment {
   val platform: Platform
}
