package io.kotest.assertions

actual val assertionCounter: AssertionCounter = JsAssertionCounter

object JsAssertionCounter : BasicAssertionCounter()
