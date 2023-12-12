package io.kotest.mpp

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.getenv
import kotlinx.cinterop.toKString

actual fun sysprop(name: String): String? = null
@OptIn(ExperimentalForeignApi::class)
actual fun env(name: String): String? = getenv(name)?.toKString()
