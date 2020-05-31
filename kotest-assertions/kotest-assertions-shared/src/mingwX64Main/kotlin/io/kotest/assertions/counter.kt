package io.kotest.assertions

@ThreadLocal
actual val assertionCounter: AssertionCounter =
   BasicAssertionCounter
