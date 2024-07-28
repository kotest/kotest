package io.kotest.core

import io.kotest.common.KotestInternal
import kotlin.time.TimeMark

@KotestInternal
actual fun writeLog(start: TimeMark, t: Throwable?, f: () -> String) {
}
