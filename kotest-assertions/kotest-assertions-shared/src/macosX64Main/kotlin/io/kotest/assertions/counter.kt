package io.kotest.assertions

import kotlin.native.concurrent.ThreadLocal

actual val assertionCounter: AssertionCounter = NativeAssertionCounter

@ThreadLocal
object NativeAssertionCounter : BasicAssertionCounter()
