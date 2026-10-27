package com.sksamuel.kotest.tests.concurrency

import kotlin.time.Duration

internal actual fun blockCurrentThread(duration: Duration) {
   Thread.sleep(duration.inWholeMilliseconds)
}
