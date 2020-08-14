package io.kotest.engine

actual fun timeInMillis(): Long = (js("Date.now()").unsafeCast<Double>()).toLong()

