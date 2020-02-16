package com.sksamuel.kotest.specs

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe

fun inlineListener(mode: IsolationMode) = funSpec {

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

   listener(a)
   listener(b)

   test("a + b should both run for isolation mode $mode") {
      count.shouldBe(3)
   }
}

class MultipleBeforeSpecListenerTest : FunSpec() {
   init {
      include(inlineListener(IsolationMode.InstancePerTest))
      include(inlineListener(IsolationMode.InstancePerLeaf))
      include(inlineListener(IsolationMode.SingleInstance))
   }
}
