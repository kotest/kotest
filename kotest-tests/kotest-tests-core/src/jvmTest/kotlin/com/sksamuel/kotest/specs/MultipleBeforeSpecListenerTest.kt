package com.sksamuel.kotest.specs

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class MultipleBeforeSpecListenerTest : FunSpec() {

   var count = 0

   val a = object : TestListener {
      override suspend fun beforeSpec(spec: Spec) {
         count++
      }
   }

   val b = object : TestListener {
      override suspend fun beforeSpec(spec: Spec) {
         count += 2
      }
   }


   init {

      listener(a)
      listener(b)

      test("a + b should both run") {
         count.shouldBe(3)
      }
   }
}
