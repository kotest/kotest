package io.kotest.mpp

import io.kotest.common.KotestInternal
import io.kotest.common.TimeMarkCompat

@KotestInternal
actual fun writeLog(start: TimeMarkCompat, t: Throwable?, f: () -> String) {
}
