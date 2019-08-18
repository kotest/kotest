package io.kotlintest

import java.time.Duration

fun <T> continually(duration: Duration, f: () -> T): T? = io.kotlintest.assertions.continually(duration.toMillis(), f)
