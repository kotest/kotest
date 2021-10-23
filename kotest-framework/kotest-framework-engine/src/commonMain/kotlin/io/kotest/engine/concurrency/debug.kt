package io.kotest.engine.concurrency

internal expect inline fun <T> withDebugProbe(f: () -> T): T
