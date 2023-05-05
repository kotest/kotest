package io.kotest

import io.kotest.assertions.throwables.shouldNotThrowAnyUnit
import io.kotest.mpp.atomics.AtomicReference
import kotlin.test.Test

class NativeAtomicReferenceTest {
   @Test
   fun testShouldWithMatcher() {
      shouldNotThrowAnyUnit { AtomicReference(1234) }
   }
}
