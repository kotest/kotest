package io.kotest.mpp

actual fun timeInMillis(): Long = (js("Date.now()").unsafeCast<Double>()).toLong()
