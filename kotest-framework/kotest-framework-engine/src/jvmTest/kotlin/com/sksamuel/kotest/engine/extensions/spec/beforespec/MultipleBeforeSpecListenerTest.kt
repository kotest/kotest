package com.sksamuel.kotest.engine.extensions.spec.beforespec

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

      extension(a)
      extension(b)

      test("a + b should both run") {
         count.shouldBe(3)
      }
   }
}
