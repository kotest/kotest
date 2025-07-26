package io.kotest.common

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

actual fun sysprop(name: String): String? = null

@OptIn(ExperimentalForeignApi::class)
actual fun env(name: String): String? = getenv(name)?.toKString()
