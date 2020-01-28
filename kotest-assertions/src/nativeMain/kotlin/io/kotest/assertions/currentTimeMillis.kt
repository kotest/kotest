package io.kotest.assertions

import kotlin.system.getTimeMillis

actual fun currentTimeMillis(): Long = getTimeMillis()
