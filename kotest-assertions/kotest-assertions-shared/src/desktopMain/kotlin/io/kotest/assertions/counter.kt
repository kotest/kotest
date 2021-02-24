package io.kotest.assertions

import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
actual val assertionCounter: AssertionCounter = BasicAssertionCounter()
