package io.kotest.mpp.atomics

import io.kotest.core.spec.style.FunSpec

class AtomicPropertyTest : FunSpec() {
   init {
      test("AtomicProperty is assignable") {
         var boolProperty: Boolean by AtomicProperty { false }
         boolProperty = true
      }
   }
}
