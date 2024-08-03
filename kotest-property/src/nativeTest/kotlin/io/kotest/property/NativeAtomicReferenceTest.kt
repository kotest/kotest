package io.kotest.property

import io.kotest.assertions.throwables.shouldNotThrowAnyUnit
import io.kotest.mpp.atomics.AtomicReference
import kotlin.test.Test

class NativeAtomicReferenceTest {
   @Test
   fun testAtomicReference() {
      shouldNotThrowAnyUnit { AtomicReference(1234) }
   }
}
