package io.kotest.core.runtime

actual fun timeInMillis(): Long = (js("Date.now()").unsafeCast<Double>()).toLong()

