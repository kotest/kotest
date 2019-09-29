package io.kotest

import java.time.Duration

fun <T> continually(duration: Duration, f: () -> T): T? = io.kotest.assertions.continually(duration.toMillis(), f)
