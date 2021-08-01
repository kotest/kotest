package io.kotest.assertions

import kotlin.native.concurrent.SharedImmutable

@SharedImmutable
actual val assertionCounter: AssertionCounter = NoopAssertionsCounter
