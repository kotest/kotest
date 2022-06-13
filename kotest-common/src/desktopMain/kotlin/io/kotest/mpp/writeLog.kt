package io.kotest.mpp

import io.kotest.common.TimeMarkCompat
import kotlin.time.ExperimentalTime

@ExperimentalTime
actual fun writeLog(start: TimeMarkCompat, t: Throwable?, f: () -> String) {
}
