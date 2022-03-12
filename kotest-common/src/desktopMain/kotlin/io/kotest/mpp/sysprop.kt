package io.kotest.mpp

import platform.posix.getenv
import kotlinx.cinterop.toKString

actual fun sysprop(name: String): String? = null
actual fun env(name: String): String? = getenv(name)?.toKString()
