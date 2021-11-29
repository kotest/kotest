package io.kotest.mpp

import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark

@ExperimentalTime
actual fun writeLog(start: TimeMark, t: Throwable?, f: () -> String) {
}
